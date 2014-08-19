package com.prunn.rfdynhud.widgets.prunn.f109.racegap;

import java.awt.Font;
import java.io.IOException;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;
import com.prunn.rfdynhud.widgets.prunn.f109.raceinfos.RaceInfosWidget;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
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
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class RaceGapWidget extends Widget
{
    private DrawnString dsPos = null;
    private DrawnString dsPos2 = null;
    private DrawnString dsName = null;
    private DrawnString dsName2 = null;
    private DrawnString dsTime = null;
    //private DrawnString dsLastGap = null;
    
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos1.png" );
    private final ImagePropertyWithTexture imgNumber = new ImagePropertyWithTexture( "imgNumber", "prunn/f109/number.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgTeam", "prunn/f109/team.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f109/qlaptime.png" );
    //private final ImagePropertyWithTexture imgLastGap = new ImagePropertyWithTexture( "imgTime", "prunn/f109/gapyellow.png" );
    
    protected final IntProperty frequency = new IntProperty("appearence frequency", "frequency", 3);
    protected final FontProperty wsbrFont = new FontProperty("Main Font", "wsbrFont");
    protected final ColorProperty BlackFontColor = new ColorProperty("Black Font Color", "BlackFontColor");
    protected final ColorProperty WhiteFontColor = new ColorProperty("White Font Color", "WhiteFontColor");
    //protected final BooleanProperty showpastgap = new BooleanProperty("Show Past Gap", "showpastgap", true);
    
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private final IntValue CurrentSector = new IntValue();
    private String  name, name2;
    private int place, place2;
    private String gap;
    
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
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
        //visibleEnd = 0x8000000000000000L;
    }
    private static final String getTimeAsGapString2( float gap )
    {
        return ( "+ " + TimingUtil.getTimeAsLaptimeString( gap ) );
    }
    private void fillvsis(ScoringInfo scoringInfo)
    {
        VehicleScoringInfo viewedvsi = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo vsi1;
        VehicleScoringInfo vsi2;
        float GapFront = 1000.000f;
        float GapBehind = 0.000f;
        int LapFront = 1000;
        int LapBehind = 0;
        
        if(viewedvsi.getNextInFront( false ) != null)
        {
            GapFront = Math.abs(viewedvsi.getTimeBehindNextInFront( false ));
            LapFront = viewedvsi.getLapsBehindNextInFront( false );
        }
        if(viewedvsi.getNextBehind( false ) != null)
        {
            GapBehind = Math.abs( viewedvsi.getNextBehind( false ).getTimeBehindNextInFront( false ));
            LapBehind = viewedvsi.getNextBehind( false ).getLapsBehindNextInFront( false );
        }
        
        if(viewedvsi.getNextBehind( false ) == null || GapFront < GapBehind || LapFront < LapBehind)
        {
            vsi1 = viewedvsi.getNextInFront( false );
            vsi2 = viewedvsi;
            if(LapFront == 0)
                gap = getTimeAsGapString2(GapFront);
            else
            {
                String laps = ( LapFront > 1 ) ? " Laps" : " Lap";
                gap = "+ " + LapFront + laps;
            }
        }
        else
        {
            vsi1 = viewedvsi;
            vsi2 = viewedvsi.getNextBehind( false );
            if(LapBehind == 0)
                gap = getTimeAsGapString2(GapBehind);
            else
            {
                String laps = ( LapBehind > 1 ) ? " Laps" : " Lap";
                gap = "+ " + LapBehind + laps;
            }
        }
        
        place = vsi1.getPlace(false);
        name = vsi1.getDriverNameShort();
        place2 = vsi2.getPlace(false);
        name2 = vsi2.getDriverNameShort();
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
        imgPos.updateSize( height / 2, height / 2, isEditorMode );
        imgPosFirst.updateSize( height / 2, height / 2, isEditorMode );
        imgNumber.updateSize( height / 2, height / 2, isEditorMode );
        imgName.updateSize( Math.round(width * 0.26f) + imgNumber.getTexture().getWidth(), height / 2, isEditorMode );
        imgTeam.updateSize( imgName.getTexture().getWidth() + imgNumber.getTexture().getWidth(), height / 2, isEditorMode );
        imgTime.updateSize( width - imgPos.getTexture().getWidth()*2 - imgName.getTexture().getWidth()*2, height / 2, isEditorMode );
        
        int fh = TextureImage2D.getStringHeight( "0yI", getFontProperty() );
        int top1 = ( height / 2 - fh ) / 2;
        
        dsPos = drawnStringFactory.newDrawnString( "dsPos", height*3/12, top1, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsName = drawnStringFactory.newDrawnString( "dsName", imgPos.getTexture().getWidth()+10, top1, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", imgPos.getTexture().getWidth() + imgName.getTexture().getWidth() + imgTime.getTexture().getWidth()/2, top1, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsPos2 = drawnStringFactory.newDrawnString( "dsPos2", width - height*3/12, top1, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsName2 = drawnStringFactory.newDrawnString( "dsName2", width - imgPos.getTexture().getWidth()-10, top1, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
         
        CurrentSector.update(scoringInfo.getViewedVehicleScoringInfo().getSector());
        
        if(isEditorMode)
        {
            fillvsis(scoringInfo);
            return true;
        }
        
        if(RaceInfosWidget.visible() || scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() < 1 || scoringInfo.getViewedVehicleScoringInfo().getFinishStatus().isFinished())
        {
            isvisible = false;
            return false;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd)
        {
            isvisible = true;
            return true;
        }
            
        
        if( CurrentSector.hasChanged() && scoringInfo.getNumVehicles() > 1)
        {
            if( (int)(Math.random()*frequency.getValue()) == 0 )
            {
                fillvsis(scoringInfo);
                visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
                isvisible = true;
                return true;
            }
        }
        isvisible = false;
        return false;
    		
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        if(place == 1)
            texture.clear( imgPosFirst.getTexture(), offsetX, offsetY, false, null );
        else
            texture.clear( imgPos.getTexture(), offsetX, offsetY, false, null );
        
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY, false, null );
        
        texture.clear( imgName.getTexture(), offsetX  + width - imgPos.getTexture().getWidth() - imgName.getTexture().getWidth(), offsetY, false, null );
        texture.clear( imgPos.getTexture(), offsetX + width - imgPos.getTexture().getWidth(), offsetY , false, null );
    
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        if ( needsCompleteRedraw  )
        {
            dsPos.draw( offsetX, offsetY, Integer.toString( place ), texture );
            dsName.draw( offsetX, offsetY, name, texture );
            dsTime.draw( offsetX, offsetY, gap , texture);
        	dsPos2.draw( offsetX, offsetY, Integer.toString( place2 ), texture );
            dsName2.draw( offsetX, offsetY, name2, texture );
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( wsbrFont, "" );
        writer.writeProperty( BlackFontColor, "" );
        writer.writeProperty( WhiteFontColor, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty(frequency, "");
        //writer.writeProperty(showpastgap, "");
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( wsbrFont ) );
        if ( loader.loadProperty( BlackFontColor ) );
        if ( loader.loadProperty( WhiteFontColor ) );
        if(!loader.loadProperty(visibleTime));
        if(!loader.loadProperty(frequency));
        //if ( loader.loadProperty( showpastgap ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( wsbrFont );
        propsCont.addProperty( BlackFontColor );
        propsCont.addProperty( WhiteFontColor );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty(frequency);
        //propsCont.addProperty(showpastgap);
        
        
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
    
    public RaceGapWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 76.0f, 19.0f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 15);
        visibleEnd = 0;
        getBackgroundProperty().setColorValue( "#00000000" );
        
    }
    
}
