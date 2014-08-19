package com.prunn.rfdynhud.widgets.prunn.f109.cpufastestlap;

import java.awt.Font;
import java.io.IOException;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class FastestSectorsWidget extends Widget
{
    private DrawnString dsPos1 = null;
    private DrawnString dsName1 = null;
    private DrawnString dsTime1 = null;
    private DrawnString dsPos2 = null;
    private DrawnString dsName2 = null;
    private DrawnString dsTime2 = null;
    private DrawnString dsPos3 = null;
    private DrawnString dsName3 = null;
    private DrawnString dsTime3 = null;
    private DrawnString dsPosF = null;
    private DrawnString dsTitle = null;
    private DrawnString dsComputed = null;
    private FloatValue time1 = new FloatValue(-1F, 0.001F);
    private FloatValue time2 = new FloatValue(-1F, 0.001F);
    private FloatValue time3 = new FloatValue(-1F, 0.001F);
    private FloatValue fastestcpu = new FloatValue(-1F, 0.001F);
    private String sec1name="", sec2name="",sec3name="";
    private int place1=0,place2=0,place3 =0;
    
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos1.png" );
    private final ImagePropertyWithTexture imgPosF = new ImagePropertyWithTexture( "imgPos", "prunn/f109/gapgreen.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f109/qlaptime.png" );
    
    protected final FontProperty wsbrFont = new FontProperty("Main Font", "wsbrFont");
    protected final ColorProperty BlackFontColor = new ColorProperty("Black Font Color", "BlackFontColor");
    protected final ColorProperty WhiteFontColor = new ColorProperty("White Font Color", "WhiteFontColor");
    
    
    
    
    public String getDefaultNamedColorValue(String name)
    {
    	if(name.equals("StandardBackground"))
            return "#000000B4";
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
        if(name.equals("wsbrFont"))
            return FontUtils.getFontString("Dialog", 1, 24, true, true);
        
        return null;
    }
    
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        
        imgPos.updateSize( height / 4, height / 4, isEditorMode );
        imgPosFirst.updateSize( height / 4, height / 4, isEditorMode );
        imgPosF.updateSize( height / 4, height / 4, isEditorMode );
        imgName.updateSize( Math.round(width * 0.6f), height / 3, isEditorMode );
        imgTime.updateSize( width - imgPos.getTexture().getWidth() - imgName.getTexture().getWidth(), height / 3, isEditorMode );
        
        dsPos1 = drawnStringFactory.newDrawnString( "dsPos1", height*3/24, imgPos.getTexture().getWidth()/2 - fh/2, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsName1 = drawnStringFactory.newDrawnString( "dsName1", imgPos.getTexture().getWidth()+10, imgPos.getTexture().getWidth()/2 - fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsTime1 = drawnStringFactory.newDrawnString( "dsTime1", imgPos.getTexture().getWidth() + imgName.getTexture().getWidth() + imgTime.getTexture().getWidth()*9/12, imgPos.getTexture().getWidth()/2 - fh/2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor());
        
        dsPos2 = drawnStringFactory.newDrawnString( "dsPos2", height*3/24, imgPos.getTexture().getWidth() + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsName2 = drawnStringFactory.newDrawnString( "dsName2", imgPos.getTexture().getWidth()+10, imgPos.getTexture().getWidth() + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsTime2 = drawnStringFactory.newDrawnString( "dsTime2", imgPos.getTexture().getWidth() + imgName.getTexture().getWidth() + imgTime.getTexture().getWidth()*9/12, imgPos.getTexture().getWidth() + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor());
        
        dsPos3 = drawnStringFactory.newDrawnString( "dsPos3", height*3/24, imgPos.getTexture().getWidth()*2 + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsName3 = drawnStringFactory.newDrawnString( "dsName3", imgPos.getTexture().getWidth()+10, imgPos.getTexture().getWidth()*2 + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsTime3 = drawnStringFactory.newDrawnString( "dsTime3", imgPos.getTexture().getWidth() + imgName.getTexture().getWidth() + imgTime.getTexture().getWidth()*9/12, imgPos.getTexture().getWidth()*2 + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor());
        
        dsPosF = drawnStringFactory.newDrawnString( "dsPosF", height*3/24, imgPos.getTexture().getWidth()*3 + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsTitle = drawnStringFactory.newDrawnString( "dsTitle", imgPos.getTexture().getWidth()+10, imgPos.getTexture().getWidth()*3 + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsComputed = drawnStringFactory.newDrawnString( "dsComputed", imgPos.getTexture().getWidth() + imgName.getTexture().getWidth() + imgTime.getTexture().getWidth()*9/12, imgPos.getTexture().getWidth()*3 + imgPos.getTexture().getWidth()/2 - fh/2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor());
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        super.updateVisibility(gameData, isEditorMode);
        
        if(gameData.getScoringInfo().getLeadersVehicleScoringInfo().getBestLapTime() > 0)
        {
            float sec1time = 800;
            float sec2time = 800;
            float sec3time = 800;
            
            VehicleScoringInfo vsi;
            for(int i=0;i < gameData.getScoringInfo().getNumVehicles();i++)
            {
                vsi = gameData.getScoringInfo().getVehicleScoringInfo( i );
                if(vsi.getFastestLaptime() != null && vsi.getFastestLaptime().getSector1() > 0 && vsi.getFastestLaptime().getSector1() <= sec1time)
                {
                    sec1time = vsi.getFastestLaptime().getSector1();
                    sec1name = vsi.getDriverNameShort();
                    place1 = vsi.getPlace( false );
                }
                if(vsi.getFastestLaptime() != null && vsi.getFastestLaptime().getSector2() > 0 && vsi.getFastestLaptime().getSector2() <= sec2time)
                {
                    sec2time = vsi.getFastestLaptime().getSector2();
                    sec2name = vsi.getDriverNameShort();
                    place2 = vsi.getPlace( false );
                }
                if(vsi.getFastestLaptime() != null && vsi.getFastestLaptime().getSector3() > 0 && vsi.getFastestLaptime().getSector3() <= sec3time)
                {
                    sec3time = vsi.getFastestLaptime().getSector3();
                    sec3name = vsi.getDriverNameShort();
                    place3 = vsi.getPlace( false );
                }
            }
                        
            time1.update( sec1time );
            time2.update( sec2time );
            time3.update( sec3time );
            
            fastestcpu.update( time1.getValue() + time2.getValue() + time3.getValue() );
            if(fastestcpu.hasChanged())
               forceCompleteRedraw(true);
            
            return true;
        }
        return false;
    		
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        if(place1 == 1)
            texture.clear( imgPosFirst.getTexture(), offsetX, offsetY, false, null );
        else
            texture.clear( imgPos.getTexture(), offsetX, offsetY, false, null );
        
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY, false, null );
        
        if(place2 == 1)
            texture.clear( imgPosFirst.getTexture(), offsetX, offsetY + imgPos.getTexture().getWidth(), false, null );
        else
            texture.clear( imgPos.getTexture(), offsetX, offsetY + imgPos.getTexture().getWidth(), false, null );
        
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth(), false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth(), false, null );
        
        if(place3 == 1)
            texture.clear( imgPosFirst.getTexture(), offsetX, offsetY + imgPos.getTexture().getWidth()*2, false, null );
        else
            texture.clear( imgPos.getTexture(), offsetX, offsetY + imgPos.getTexture().getWidth()*2, false, null );
        
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth()*2, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth()*2, false, null );
        
        texture.clear( imgPosF.getTexture(), offsetX, offsetY + imgPos.getTexture().getWidth()*3, false, null );
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth()*3, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth()*3, false, null );
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        
         
        if ( needsCompleteRedraw || time1.hasChanged())
        {
            dsPos1.draw( offsetX, offsetY, Integer.toString( place1 ), texture );
            dsName1.draw( offsetX, offsetY, sec1name, texture );
            dsTime1.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( time1.getValue() ), texture);
        }
        if ( needsCompleteRedraw || time2.hasChanged())
        {
            dsPos2.draw( offsetX, offsetY, Integer.toString( place2 ), texture );
            dsName2.draw( offsetX, offsetY, sec2name, texture );
            dsTime2.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( time2.getValue() ), texture);
        }
        if ( needsCompleteRedraw || time3.hasChanged())
        {   
            dsPos3.draw( offsetX, offsetY, Integer.toString( place3 ), texture );
            dsName3.draw( offsetX, offsetY, sec3name, texture );
            dsTime3.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( time3.getValue() ), texture);
        }
        if ( needsCompleteRedraw || fastestcpu.hasChanged() )
        {
            dsPosF.draw( offsetX, offsetY, "F", texture );
            dsTitle.draw( offsetX, offsetY, "Computed Best Time", texture );
            dsComputed.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( fastestcpu.getValue() ), texture);
        }
         
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( wsbrFont, "" );
        writer.writeProperty( BlackFontColor, "" );
        writer.writeProperty( WhiteFontColor, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( wsbrFont ) );
        if ( loader.loadProperty( BlackFontColor ) );
        if ( loader.loadProperty( WhiteFontColor ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( wsbrFont );
        propsCont.addProperty( BlackFontColor );
        propsCont.addProperty( WhiteFontColor );
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
    
    public FastestSectorsWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 42.0f, 18.5f  );
        
       
    }
    
}
