package com.prunn.rfdynhud.widgets.prunn.f109.tracktemps;

import java.awt.Font;
import java.io.IOException;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
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
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class TrackTempsWidget extends Widget
{
    private DrawnString dsAmbient = null;
    private DrawnString dsAmbientTemp = null;
    private DrawnString dsTrack = null;
    private DrawnString dsTrackTemp = null;
    
    private IntValue AmbientTemp = new IntValue();
    private IntValue TrackTemp = new IntValue();
    
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
        
        imgName.updateSize( Math.round(width * 0.68f), height / 2, isEditorMode );
        imgTime.updateSize( width - imgName.getTexture().getWidth(), height / 2, isEditorMode );
        
        dsAmbient = drawnStringFactory.newDrawnString( "dsAmbient", 10, fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsAmbientTemp = drawnStringFactory.newDrawnString( "dsAmbientTemp", imgName.getTexture().getWidth() + imgTime.getTexture().getWidth()*9/12, fh/2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "°C");
        
        dsTrack = drawnStringFactory.newDrawnString( "dsTrack", 10, height/2 + fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsTrackTemp = drawnStringFactory.newDrawnString( "dsTrackTemp", imgName.getTexture().getWidth() + imgTime.getTexture().getWidth()*9/12, height/2 + fh/2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "°C");
        
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        texture.clear( imgName.getTexture(), offsetX, offsetY, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgName.getTexture().getWidth(), offsetY, false, null );
        
        texture.clear( imgName.getTexture(), offsetX, offsetY + height/2, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgName.getTexture().getWidth(), offsetY + height/2, false, null );
     
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        
        AmbientTemp.update((int)Math.floor(gameData.getWeatherInfo().getAmbientTemperature()));
        TrackTemp.update((int)Math.floor(gameData.getWeatherInfo().getTrackTemperature()));
        
        if ( needsCompleteRedraw || AmbientTemp.hasChanged())
        {
            dsAmbient.draw( offsetX, offsetY, "Air Temperature", texture );
            dsAmbientTemp.draw( offsetX, offsetY, AmbientTemp.getValueAsString(), texture);
        }
        if ( needsCompleteRedraw || TrackTemp.hasChanged())
        {
            dsTrack.draw( offsetX, offsetY, "Track Temperature" , texture );
            dsTrackTemp.draw( offsetX, offsetY, TrackTemp.getValueAsString(), texture);
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
    
    public TrackTempsWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 28.0f, 10.0f );
       
    }
    
}
