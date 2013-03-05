package com.prunn.rfdynhud.widgets.prunn.f109.raceinfos;

import java.awt.Font;
import java.io.IOException;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;

import com.prunn.rfdynhud.widgets.prunn.f109.racegap.RaceGapWidget;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class RaceInfos2Widget extends Widget
{
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    
    private DrawnString dsPos = null;
    private DrawnString dsName = null;
    private DrawnString dsBest = null;
    private DrawnString dsLast = null;
    private DrawnString dsGainedPlaces = null;
    private DrawnString dsBestTime = null;
    private DrawnString dsLastTime = null;
    private DrawnString dsPitStops = null;
    
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos.png" );
    private final ImagePropertyWithTexture imgPos1 = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos1.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgTeam", "prunn/f109/team.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f109/qlaptime.png" );
    private final ImagePropertyWithTexture imgTitle = new ImagePropertyWithTexture( "imgTitle", "prunn/f109/title.png" );
    private final ImagePropertyWithTexture imgPositive = new ImagePropertyWithTexture( "imgTime", "prunn/f109/pospositive.png" );
    private final ImagePropertyWithTexture imgNegative = new ImagePropertyWithTexture( "imgTime", "prunn/f109/posnegative.png" );
    private final ImagePropertyWithTexture imgNeutral = new ImagePropertyWithTexture( "imgTime", "prunn/f109/posneutral.png" );
    private final ImagePropertyWithTexture imgPitStops = new ImagePropertyWithTexture( "imgPitStops", "prunn/f109/qlaptime.png" );
    
    
    private final FontProperty posFont = new FontProperty("positionFont", "posFont");
    protected final FontProperty wsbrFont = new FontProperty("Main Font", "wsbrFont");
    protected final ColorProperty BlackFontColor = new ColorProperty("Black Font Color", "BlackFontColor");
    protected final ColorProperty WhiteFontColor = new ColorProperty("White Font Color", "WhiteFontColor");
    
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private boolean startedPositionsInitialized = false;
    private int[] startedPositions = null;
    short gainedPlaces;
    private final IntValue CurrentLap = new IntValue();
    
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
        if(name.equals("posFont"))
            return FontUtils.getFontString("Dialog", 1, 48, true, true);
        return null;
    }
    
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
        
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
        int rowHeight = height / 2;
        int fw2 = Math.round(width * 0.47f);
        int fw2b = Math.round(width * 0.20f);
        int fw3 = width - fw2;
        int fw3c = (width - fw2 - rowHeight)/2;// fw3b/2;
        imgPos.updateSize( rowHeight, rowHeight, isEditorMode );
        imgPos1.updateSize( rowHeight, rowHeight, isEditorMode );
        imgName.updateSize( fw2, rowHeight, isEditorMode );
        imgTeam.updateSize( fw2b, rowHeight, isEditorMode );
        imgTime.updateSize( (width - imgTeam.getTexture().getWidth()*2)/2, rowHeight, isEditorMode );
        imgTitle.updateSize( fw3, rowHeight, isEditorMode );
        imgPositive.updateSize( fw3c, rowHeight, isEditorMode );
        imgNegative.updateSize( fw3c, rowHeight, isEditorMode );
        imgNeutral.updateSize( fw3c, rowHeight, isEditorMode );
        imgPitStops.updateSize( fw3c, rowHeight, isEditorMode );
        
        int top1 = ( rowHeight - fh ) / 2;
        int top2 = ( rowHeight - fh ) / 2 + rowHeight;
        
        dsPos = drawnStringFactory.newDrawnString( "dsPos", rowHeight/2, top1, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsName = drawnStringFactory.newDrawnString( "dsName", Math.round(rowHeight*1.25f), top1, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsBest = drawnStringFactory.newDrawnString( "dsBest", imgTeam.getTexture().getWidth()/2, top2, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsLast = drawnStringFactory.newDrawnString( "dsLast", width / 2 + imgTeam.getTexture().getWidth()/2, top2, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsBestTime = drawnStringFactory.newDrawnString( "dsBestTime", width/2 - 10, top2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsLastTime = drawnStringFactory.newDrawnString( "dsLastTime", width - 10, top2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsPitStops = drawnStringFactory.newDrawnString( "dsPitStops", width - 10, top1, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsGainedPlaces = drawnStringFactory.newDrawnString( "dsGainedPlaces", width - 100, top1, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        
    }
    private void initStartedFromPositions( ScoringInfo scoringInfo )
    {
        startedPositions = new int[scoringInfo.getNumVehicles()];
        
        for(int j=0;j < scoringInfo.getNumVehicles(); j++)
            startedPositions[j] = scoringInfo.getVehicleScoringInfo( j ).getDriverId();
        
        startedPositionsInitialized = true;
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if ( !startedPositionsInitialized )
            initStartedFromPositions( scoringInfo );
        
        CurrentLap.update(scoringInfo.getViewedVehicleScoringInfo().getLapsCompleted());
        
        //if(RaceInfosWidget.visible() || RaceGapWidget.visible())
        //  return false;
        
        if(CurrentLap.hasChanged() && scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() / scoringInfo.getMaxLaps() > 0.6f && !RaceInfosWidget.visible() && !RaceGapWidget.visible())
        {
            VehicleScoringInfo vsi = scoringInfo.getViewedVehicleScoringInfo();
            
            int startedfrom=0;
            for(int p=0; p < scoringInfo.getNumVehicles(); p++)
            {
                if( vsi.getDriverId() == startedPositions[p] )
                {
                    startedfrom = p+1;
                    break;
                } 
            }
            gainedPlaces = (short)( startedfrom - vsi.getPlace( false ) );
            
            forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd )
        {
            //forceCompleteRedraw(true);
            isvisible = true;
            return true;
        }
        
        
        isvisible = false;
        return false;	
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        int rowHeight = height / 2;
        
        if(gainedPlaces > 0 || isEditorMode)
            texture.clear( imgPositive.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY, true, null ); 
        else 
            if(gainedPlaces < 0)
                texture.clear( imgNegative.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY, true, null ); 
            else
                texture.clear( imgNeutral.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY, true, null ); 
            
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY, false, null );
        texture.clear( imgPitStops.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth() + imgPositive.getTexture().getWidth(), offsetY, true, null ); 
        
        if(gameData.getScoringInfo().getViewedVehicleScoringInfo().getNextInFront( false ) == null)
            texture.clear( imgPos1.getTexture(), offsetX, offsetY, false, null );
        else
            texture.clear( imgPos.getTexture(), offsetX, offsetY, false, null );
        
        texture.clear( imgTeam.getTexture(), offsetX, offsetY + rowHeight, false, null );
        texture.clear( imgTeam.getTexture(), offsetX + width/2, offsetY + rowHeight, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgTeam.getTexture().getWidth(), offsetY + rowHeight, false, null );
        texture.clear( imgTime.getTexture(), offsetX + width/2 + imgTeam.getTexture().getWidth(), offsetY + rowHeight, false, null );
        
                    
        
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
    	sessionTime.update(scoringInfo.getSessionTime());
    	
    	if ( needsCompleteRedraw )
        {
    	    VehicleScoringInfo vsi = scoringInfo.getViewedVehicleScoringInfo();
            int numpit = vsi.getNumPitstopsMade();
            String plural = ( numpit > 1 ) ? " Stops" : " Stop";
    	    String pitstops = Integer.toString( numpit ) + plural;
             
            dsPos.draw( offsetX, offsetY, Integer.toString( vsi.getPlace( false ) ), texture );
            dsName.draw( offsetX, offsetY, vsi.getDriverNameShort(), texture );
            dsBest.draw( offsetX, offsetY, "Best", texture );
            dsBestTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( vsi.getBestLapTime() ), texture );
            dsLast.draw( offsetX, offsetY, "Last", texture );
            dsLastTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( vsi.getLastLapTime() ), texture );
            dsPitStops.draw( offsetX, offsetY, pitstops, texture );
            dsGainedPlaces.draw( offsetX, offsetY, String.valueOf( Math.abs( gainedPlaces )), texture );
            
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( wsbrFont, "" );
        writer.writeProperty( posFont, "" );
        writer.writeProperty( BlackFontColor, "" );
        writer.writeProperty( WhiteFontColor, "" );
        writer.writeProperty(visibleTime, "");
        
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( wsbrFont ) );
        if ( loader.loadProperty( posFont ) );
        if ( loader.loadProperty( BlackFontColor ) );
        if ( loader.loadProperty( WhiteFontColor ) );
        if(!loader.loadProperty(visibleTime));
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( wsbrFont );
        propsCont.addProperty( posFont );
        propsCont.addProperty( BlackFontColor );
        propsCont.addProperty( WhiteFontColor );
        propsCont.addProperty(visibleTime);
        
        
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
    
    public RaceInfos2Widget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 50.0f, 20.5f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0;
        getBackgroundProperty().setColorValue( "#00000000" );
    }
    
}
