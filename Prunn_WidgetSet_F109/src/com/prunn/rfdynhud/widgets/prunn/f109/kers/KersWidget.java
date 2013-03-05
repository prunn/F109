package com.prunn.rfdynhud.widgets.prunn.f109.kers;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImageProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.ImageTemplate;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.TransformableTexture;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;



/**
 * 
 * 
 * @author Prunn 2011
 */


public class KersWidget extends Widget
{
    private TransformableTexture texThrottle1;
    private TransformableTexture texThrottle2;
    private TransformableTexture texBrake1;
    private TransformableTexture texBrake2;
    private TransformableTexture texKers1;
    private TransformableTexture texKers2;
    private TransformableTexture texKersOn1;
    private TransformableTexture texKersOn2;
    //private TransformableTexture texRPM;
    private ImageProperty Imgkers = new ImageProperty("Imgkers", null, "prunn/f109/kers.png", false, false);
    private ImageProperty ImgkersOn = new ImageProperty("ImgkersOn", null, "prunn/f109/kerson.png", false, false);
    private final ImagePropertyWithTexture ImgRevmetter = new ImagePropertyWithTexture( "image", null, "prunn/f109/revmeterbg.png", false, false ); 
    //private final ImagePropertyWithTexture ImgRevs = new ImagePropertyWithTexture( "image", null, "prunn/f109/revmeterrpm.png", false, false ); 
    private final ImageProperty ImgBrake = new ImageProperty("ImgBrake", null, "prunn/f109/brake.png", false, false);
    private final ImageProperty ImgThrottle = new ImageProperty("ImgThrottle", null, "prunn/f109/throttle.png", false, false);
    //private ImageProperty ImgSpeed = new ImageProperty("ImgSpeed", null, "prunn/f109/speed.png", false, false);
    private boolean kersDirty;
    private boolean kersOnDirty;
    private boolean throttleDirty = false;
    private boolean BrakeDirty = false;
    
    private DrawnString dsGear = null;
    private DrawnString dsGear2 = null;
    private final IntValue CurrentGear = new IntValue();
    private final IntValue CurrentRPM = new IntValue();
    //private final IntValue MaxRPM = new IntValue();
    private final IntValue CurrentLap = new IntValue();
    protected final IntProperty kerstime = new IntProperty("Kers Time", 7);
    private float KersLeft;
    private final DelayProperty freezeTime;
    //private long freezeEnd;
    protected final FontProperty TBFont = new FontProperty("Main Font", "TBFont");
    protected final FontProperty GearFont = new FontProperty("Main Font", "GearFont");
    protected final ColorProperty BlackFontColor = new ColorProperty("Black Font Color", "BlackFontColor");
    protected final ColorProperty WhiteFontColor = new ColorProperty("White Font Color", "WhiteFontColor");
    
    public String getDefaultNamedColorValue(String name)
    {
        if(name.equals("StandardBackground"))
            return "#00000000";
        if(name.equals("BlackFontColor"))
            return "#2D2D2D";
        if(name.equals("WhiteFontColor"))
            return "#FFFFFF";
        if(name.equals("StandardFontColor"))
            return "#FFFFFF";

        return null;
    }
    
    @Override
    public String getDefaultNamedFontValue(String name)
    {
        if(name.equals("StandardFont"))
            return FontUtils.getFontString("Dialog", 1, 16, true, true);
        if(name.equals("TBFont"))
            return FontUtils.getFontString("Dialog", 1, 24, true, true);
        if(name.equals("GearFont"))
            return FontUtils.getFontString("Dialog", 1, 82, true, true);
        
        return null;
    }
    
    protected Boolean onVehicleControlChanged(VehicleScoringInfo viewedVSI, LiveGameData gameData, boolean isEditorMode)
    {
        super.onVehicleControlChanged(viewedVSI, gameData, isEditorMode);
        
        return viewedVSI.isPlayer();
    }
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
        
    }
    private void drawBarLabel(String label, TextureImage2D texture, int offsetX, int offsetY, int width, int height)
    {
        Rectangle2D bounds = TextureImage2D.getStringBounds(label, TBFont);
        int lblOff = 9;
        if((double)lblOff > -bounds.getWidth() && lblOff < width)
            texture.drawString(label, offsetX + lblOff, (offsetY + (height - (int)bounds.getHeight()) / 2) - (int)bounds.getY(), bounds, TBFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), true, null);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
        
        int w = widgetInnerWidth*41/100;
        int h = widgetInnerHeight*12/100;
        
        
        if(texThrottle1 == null || texThrottle1.getWidth() != w || texThrottle1.getHeight() != h || throttleDirty)
        {
            texThrottle1 = TransformableTexture.getOrCreate(w, h, true, texThrottle1, isEditorMode);
            texThrottle2 = TransformableTexture.getOrCreate(w, h, true, texThrottle2, isEditorMode);
            ImageTemplate it = ImgThrottle.getImage();
            it.drawScaled(0, 0, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h, texThrottle1.getTexture(), true);
            it.drawScaled(0, it.getBaseHeight() / 2, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h, texThrottle2.getTexture(), true);
            drawBarLabel("Throttle", texThrottle1.getTexture(), 0, 0, texThrottle1.getWidth(), texThrottle1.getHeight());
            drawBarLabel("Throttle", texThrottle2.getTexture(), 0, 0, texThrottle2.getWidth(), texThrottle2.getHeight());
            texThrottle1.setTranslation(widgetInnerWidth*56/100, widgetInnerHeight*53/100);
            texThrottle2.setTranslation(widgetInnerWidth*56/100, widgetInnerHeight*53/100);
            texThrottle1.setLocalZIndex(501);
            texThrottle2.setLocalZIndex(502);
            throttleDirty = false;
        }
        collector.add(texThrottle1);
        collector.add(texThrottle2);
        
        if(texBrake1 == null || texBrake1.getWidth() != w || texBrake1.getHeight() != h || BrakeDirty)
        {
            texBrake1 = TransformableTexture.getOrCreate(w, h, true, texBrake1, isEditorMode);
            texBrake2 = TransformableTexture.getOrCreate(w, h, true, texBrake2, isEditorMode);
            ImageTemplate it = ImgBrake.getImage();
            it.drawScaled(0, 0, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h, texBrake1.getTexture(), true);
            it.drawScaled(0, it.getBaseHeight() / 2, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h, texBrake2.getTexture(), true);
            drawBarLabel("Brake", texBrake1.getTexture(), 0, 0, texBrake1.getWidth(), texBrake1.getHeight());
            drawBarLabel("Brake", texBrake2.getTexture(), 0, 0, texBrake2.getWidth(), texBrake2.getHeight());
            texBrake1.setTranslation(widgetInnerWidth*56/100, widgetInnerHeight*64/100);
            texBrake2.setTranslation(widgetInnerWidth*56/100, widgetInnerHeight*64/100);
            texBrake1.setLocalZIndex(501);
            texBrake2.setLocalZIndex(502);
            BrakeDirty = false;
        }
        collector.add(texBrake1);
        collector.add(texBrake2);
        
        if(texKers1 == null || texKers1.getWidth() != w || texKers1.getHeight() != h || kersDirty)
        {
            texKers1 = TransformableTexture.getOrCreate(w, h - widgetInnerHeight/80, true, texKers1, isEditorMode);
            texKers2 = TransformableTexture.getOrCreate(w, h - widgetInnerHeight/80, true, texKers2, isEditorMode);
            ImageTemplate it = Imgkers.getImage();
            it.drawScaled(0, 0, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h - widgetInnerHeight/80, texKers1.getTexture(), true);
            it.drawScaled(0, it.getBaseHeight() / 2, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h - widgetInnerHeight/80, texKers2.getTexture(), true);
            texKers1.setTranslation(widgetInnerWidth*54/100, widgetInnerHeight*82/100);
            texKers2.setTranslation(widgetInnerWidth*54/100, widgetInnerHeight*82/100);
            texKers1.setLocalZIndex(501);
            texKers2.setLocalZIndex(502);
            kersDirty = false;
        }
        collector.add(texKers1);
        collector.add(texKers2);
        
        if(texKersOn1 == null || texKersOn1.getWidth() != w || texKersOn1.getHeight() != h || kersOnDirty)
        {
            texKersOn1 = TransformableTexture.getOrCreate(w, h - widgetInnerHeight/80, true, texKersOn1, isEditorMode);
            texKersOn2 = TransformableTexture.getOrCreate(w, h - widgetInnerHeight/80, true, texKersOn2, isEditorMode);
            ImageTemplate it = ImgkersOn.getImage();
            it.drawScaled(0, 0, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h - widgetInnerHeight/80, texKersOn1.getTexture(), true);
            it.drawScaled(0, it.getBaseHeight() / 2, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, w, h - widgetInnerHeight/80, texKersOn2.getTexture(), true);
            texKersOn1.setTranslation(widgetInnerWidth*54/100, widgetInnerHeight*82/100);
            texKersOn2.setTranslation(widgetInnerWidth*54/100, widgetInnerHeight*82/100);
            texKersOn1.setLocalZIndex(501);
            texKersOn2.setLocalZIndex(502);
            kersOnDirty = false;
        }
        collector.add(texKersOn1);
        collector.add(texKersOn2);
        

        
        /*if(texRPM == null || texRPM.getWidth() != w || texRPM.getHeight() != h)
        {
            texRPM = ImgRevs.getImage().getScaledTransformableTexture( widgetInnerWidth, widgetInnerHeight, texRPM, isEditorMode );
            texRPM.setLocalZIndex(504);
        }
        collector.add( texRPM );*/
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int fh = TextureImage2D.getStringHeight( "0", GearFont );
        texKersOn1.setVisible(false);
        texKersOn2.setVisible(false);
        dsGear = drawnStringFactory.newDrawnString( "dsGear", width*46/100, height/2 - fh/2, Alignment.CENTER, false, GearFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsGear2 = drawnStringFactory.newDrawnString( "dsGear", width*46/100+4, height/2 - fh/2+3, Alignment.CENTER, false, GearFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        if ( !ImgRevmetter.isNoImage() )
        {
            ImgRevmetter.updateSize( width, height, isEditorMode );
        }
        CurrentGear.update( -1 );
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        CurrentLap.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getLapsCompleted());
        
        if(gameData.getScoringInfo().getViewedVehicleScoringInfo().isPlayer())
        {
           if(CurrentLap.hasChanged())
               KersLeft = kerstime.getValue();
           
           return true; 
        }
        return false;
         
    }
    
    
    
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        texture.drawImage( ImgRevmetter.getTexture(), offsetX, offsetY, true, null );
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        

        float uThrottle = isEditorMode ? 0.4F : gameData.getTelemetryData().getUnfilteredThrottle();
        float uBrake = isEditorMode ? 0.4F : gameData.getTelemetryData().getUnfilteredBrake();
        float uKers = isEditorMode ? 0.4F : KersLeft / kerstime.getValue();
        //float uRPM = isEditorMode ? 1.0F : gameData.getTelemetryData().getEngineRPM() / gameData.getSetup().getEngine().getRevLimit();
        int wT = texThrottle2.getWidth();
        int w = texKers2.getWidth();
        int throttle = (int)((float)wT * uThrottle);
        int brake = (int)((float)wT * uBrake);
        int kers = (int)((float)w * uKers);
        
        texThrottle2.setClipRect(0, 0, throttle, texThrottle2.getHeight(), true);
        texBrake2.setClipRect(0, 0, brake, texBrake2.getHeight(), true);
        texKers2.setClipRect(0, 0, kers, texKers2.getHeight(), true);
        texKersOn2.setClipRect(0, 0, kers, texKersOn2.getHeight(), true);  
      
        
        if(gameData.getTelemetryData().getTemporaryBoostFlag() && KersLeft > 0.1)
        {
            texKersOn1.setVisible(true);
            texKersOn2.setVisible(true);
            texKers1.setVisible(false);
            texKers2.setVisible(false);
            KersLeft = KersLeft - gameData.getTelemetryData().getDeltaTime();
        }
        else
        {
            texKers1.setVisible(true);
            texKers2.setVisible(true);
            texKersOn1.setVisible(false);
            texKersOn2.setVisible(false);
        }
        
        CurrentGear.update( gameData.getTelemetryData().getCurrentGear() );
        
        if(CurrentGear.hasChanged())
        {
            if(CurrentGear.getValue() >= 0)
            {
                dsGear2.draw( offsetX, offsetY, "", texture );
                dsGear.draw( offsetX, offsetY, "", texture);
                dsGear2.draw( offsetX, offsetY, Integer.toString(CurrentGear.getValue()), texture, false  );
                dsGear.draw( offsetX, offsetY, Integer.toString(CurrentGear.getValue()), texture, false );
            }
            else
            {
                dsGear2.draw( offsetX, offsetY, "", texture );
                dsGear.draw( offsetX, offsetY, "", texture );
                dsGear2.draw( offsetX, offsetY, "R", texture, false );
                dsGear.draw( offsetX, offsetY, "R", texture, false );
            }
        }
        
        CurrentRPM.update( (int)gameData.getTelemetryData().getEngineRPM() );

        //calculate freeze
        //MaxRPM.update( (int)gameData.getTelemetryData().getEngineRPM() );
        
        //if(MaxRPM.getOldValue() > MaxRPM.getValue())
        //{
        //    freezeEnd = gameData.getScoringInfo().getSessionNanos() + freezeTime.getDelayNanos();
        //}
        //MaxRPM.hasChanged();
        ////////////////////////////////////
        //if(CurrentRPM.hasChanged())
        //{
            //int size = 1;
            //int offx = width*14/100;
            //int offy = height*15/100;
            //Texture2DCanvas canvas = texture.getTextureCanvas();
            //canvas.setAntialiazingEnabled( true );
            //canvas.setColor( Color.blue );
            //canvas.setStroke( new BasicStroke( size ) );
            //canvas.drawArc( offx, offy, width - offx*2 - width*11/100, height - offy*2, 270,(int)(uRPM * -260) );
            
            //  
        //}
        
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( kerstime, "" );
        writer.writeProperty( TBFont, "" );
        writer.writeProperty( GearFont, "" );
        writer.writeProperty( BlackFontColor, "" );
        writer.writeProperty( WhiteFontColor, "" );
        writer.writeProperty(freezeTime, "");
        
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( kerstime ) );
        if ( loader.loadProperty( TBFont ) );
        if ( loader.loadProperty( GearFont ) );
        if ( loader.loadProperty( BlackFontColor ) );
        if ( loader.loadProperty( WhiteFontColor ) );
        if(!loader.loadProperty(freezeTime));
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Misc" );
        propsCont.addProperty( kerstime );
        propsCont.addProperty( TBFont );
        propsCont.addProperty( GearFont );
        propsCont.addProperty( BlackFontColor );
        propsCont.addProperty( WhiteFontColor );
        propsCont.addProperty(freezeTime);
    }
    @Override
    protected boolean canHaveBorder()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        
        getFontProperty().setFont( "Dialog", Font.PLAIN, 6, false, true );
        
    }
    
    public KersWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 17.0f, 9.3f );
        freezeTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 1);
        //freezeEnd = 0;
        
    }
    
}
