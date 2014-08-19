package com.prunn.rfdynhud.widgets.prunn.f109.timer;

import java.awt.Font;
import java.io.IOException;
import net.ctdp.rfdynhud.gamedata.GamePhase;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.SessionLimit;
import net.ctdp.rfdynhud.gamedata.SessionType;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.properties.StringProperty;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.EnumValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;

/**
 * World Series by Renault Lap Counter
 * 
 * @author Prunn
 */
public class SessionTimerWidget extends Widget
{
	
	private final EnumValue<YellowFlagState> SCState = new EnumValue<YellowFlagState>();
    private final EnumValue<GamePhase> gamePhase = new EnumValue<GamePhase>();
    private final IntValue LapsLeft = new IntValue();
    private final BoolValue sectorYellowFlag = new BoolValue();
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    private DrawnString dsSession = null;
    private DrawnString dsSession2 = null;
    private DrawnString dsInfo = null;
    private String strlaptime = "";
    private String strlaptime2 = "";
    private String strInfo = "";
    private final StringValue strLaptime = new StringValue( "" );
    private final ImagePropertyWithTexture imgBG = new ImagePropertyWithTexture( "imgBG", "prunn/f109/laptime.png" );
    private final ImagePropertyWithTexture imgBGYellow = new ImagePropertyWithTexture( "imgBGYellow", "prunn/f109/laptimeyellow.png" );
    private final ImagePropertyWithTexture imgBGGreen = new ImagePropertyWithTexture( "imgBGGreen", "prunn/f109/laptimegreen.png" );
    private final ImagePropertyWithTexture imgQual = new ImagePropertyWithTexture( "imgQual", "prunn/f109/pos.png" );
    private final ImagePropertyWithTexture imgSC = new ImagePropertyWithTexture( "imgSC", "prunn/f109/laptimeyellow.png" );
    protected final FontProperty timeFont = new FontProperty("Timer Font", "timeFont");
    protected final FontProperty timeFont2 = new FontProperty("Timer Font Over", "timeFont2");
    protected final ColorProperty TimeFontColor = new ColorProperty("Time Font Color", "TimeFontColor");
    protected final ColorProperty YflagFontColor = new ColorProperty("Yel Flag Font Color", "YflagFontColor");
    protected final ColorProperty GflagFontColor = new ColorProperty("Green Flag Font Color", "GflagFontColor");
    protected final StringProperty strPractice1 = new StringProperty("Practice 1", "Q1");
    protected final StringProperty strPractice2 = new StringProperty("Practice 2", "Q2");
    protected final StringProperty strPractice3 = new StringProperty("Practice 3", "");
    protected final StringProperty strPractice4 = new StringProperty("Practice 4", "");
    protected final StringProperty strQualif = new StringProperty("Qualification", "Q3");
    private ColorProperty drawnFontColor;
    private ColorProperty InfoFontColor;
    
    
    public String getDefaultNamedColorValue(String name)
    {
        
        if(name.equals("StandardFontColor"))
            return "#FFFFFF";
        if(name.equals("TimeFontColor"))
            return "#0A0A0A";
        if(name.equals("YflagFontColor"))
            return "#0A0A0A";
        if(name.equals("GflagFontColor"))
            return "#FAFAFA";        
        return null;
    }
    @Override
    public String getDefaultNamedFontValue(String name)
    {
        if(name.equals("StandardFont"))
            return FontUtils.getFontString("Dialog", 1, 24, true, true);
        if(name.equals("timeFont"))
            return FontUtils.getFontString("Dialog", 1, 24, true, true);
        if(name.equals("timeFont2"))
            return FontUtils.getFontString("Dialog", 1, 14, true, true);
                
        return null;
    }
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        LapsLeft.reset();
    	sectorYellowFlag.reset();
    	SCState.reset();
        gamePhase.reset();
        super.onCockpitEntered( gameData, isEditorMode );
        
    }
    public void onSessionStarted(SessionType sessionType, LiveGameData gameData, boolean isEditorMode)
    {
        super.onSessionStarted(sessionType, gameData, isEditorMode);
        gamePhase.reset();
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
    	//int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        dsSession = drawnStringFactory.newDrawnString( "dsSession",(width + height)/ 2 , height / 6, Alignment.CENTER, false, timeFont.getFont(), isFontAntiAliased(), TimeFontColor.getColor(), null, "" );
        dsSession2 = drawnStringFactory.newDrawnString( "dsSession",(width + height)/ 2, height / 6, Alignment.CENTER, false, timeFont2.getFont(), isFontAntiAliased(), TimeFontColor.getColor(), null, "" );
        dsInfo = drawnStringFactory.newDrawnString( "dsInfo", height/2, height / 6, Alignment.CENTER, false, timeFont.getFont(), isFontAntiAliased(), GflagFontColor.getColor() );
        imgQual.updateSize( height, height, isEditorMode );
        imgSC.updateSize( height, height, isEditorMode );
        imgBG.updateSize( width - height, height, isEditorMode );
        imgBGYellow.updateSize( width - height, height, isEditorMode );
        imgBGGreen.updateSize( width - height, height, isEditorMode );
        
    }
    
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        SCState.update(scoringInfo.getYellowFlagState());
        sectorYellowFlag.update(scoringInfo.getSectorYellowFlag(scoringInfo.getViewedVehicleScoringInfo().getSector()));
        
        if(SCState.getValue() != YellowFlagState.NONE && SCState.getValue() != YellowFlagState.RESUME)
            strInfo = "SC";
        else
            if( scoringInfo.getSessionType() == SessionType.QUALIFYING1 || isEditorMode)
                strInfo = strQualif.getValue();
            else
                if( scoringInfo.getSessionType() == SessionType.PRACTICE1)
                    strInfo = strPractice1.getValue();
                else
                    if( scoringInfo.getSessionType() == SessionType.PRACTICE2)
                        strInfo = strPractice2.getValue();
                    else
                        if( scoringInfo.getSessionType() == SessionType.PRACTICE3)
                            strInfo = strPractice3.getValue();
                        else
                            if( scoringInfo.getSessionType() == SessionType.PRACTICE4)
                                strInfo = strPractice4.getValue();
                            else
                                strInfo = "";
        
        if((SCState.hasChanged() || sectorYellowFlag.hasChanged()) && !isEditorMode)
            forceCompleteRedraw(true);
        
        if( scoringInfo.getGamePhase() == GamePhase.FORMATION_LAP )
        {
            return false;
        }
        if( scoringInfo.getGamePhase() == GamePhase.STARTING_LIGHT_COUNTDOWN_HAS_BEGUN && scoringInfo.getEndTime() <= scoringInfo.getSessionTime() )
        {
            return false;
        }
        return true;
        
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        //bg part
        if(sectorYellowFlag.getValue() && (SCState.getValue() == YellowFlagState.NONE || SCState.getValue() == YellowFlagState.RESUME))
            texture.clear( imgBGYellow.getTexture(), offsetX + height, offsetY, false, null );
        else
            if(SCState.getValue() == YellowFlagState.RESUME)
                texture.clear( imgBGGreen.getTexture(), offsetX + height, offsetY, false, null );
            else
                texture.clear( imgBG.getTexture(), offsetX + height, offsetY, false, null );
            
                
        //first square part
        if(strInfo != "")
        {
            if(strInfo == "SC")
                texture.clear( imgSC.getTexture(), offsetX, offsetY, false, null );
            else
                texture.clear( imgQual.getTexture(), offsetX, offsetY, false, null );
        }
        else
            texture.clear(offsetX, offsetY, height, height, true, null);
        
            
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        
        
        if (gameData.getScoringInfo().getSessionType().isRace() && gameData.getScoringInfo().getViewedVehicleScoringInfo().getSessionLimit() == SessionLimit.LAPS)
    	{
            LapsLeft.update(gameData.getScoringInfo().getMaxLaps() - gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapsCompleted());
	    	
	    	if ( needsCompleteRedraw || LapsLeft.hasChanged())
	    	    strlaptime = LapsLeft.getValueAsString() + " / " + gameData.getScoringInfo().getMaxLaps();
			
		}
    	else // Test day only
    		if(gameData.getScoringInfo().getSessionType().isTestDay())
    		    strlaptime = "00:00:00";
    		else // any other timed session (Race, Qualify, Practice)
	    	{
	    		gamePhase.update(gameData.getScoringInfo().getGamePhase());
		    	sessionTime.update(gameData.getScoringInfo().getSessionTime());
	    		float endTime = gameData.getScoringInfo().getEndTime();
	    		
	    		if ( needsCompleteRedraw || sessionTime.hasChanged() )
		        {
		        	if(gamePhase.getValue() == GamePhase.SESSION_OVER || (endTime <= sessionTime.getValue() && gamePhase.getValue() != GamePhase.STARTING_LIGHT_COUNTDOWN_HAS_BEGUN ) )
			        {
		        	        strlaptime = "";
		        			strlaptime2 = "SESSION OVER";
		        			dsSession2.draw( offsetX, offsetY, strlaptime2,drawnFontColor.getColor(), texture );
            
			        }
		        	else
        			    if(gamePhase.getValue() == GamePhase.STARTING_LIGHT_COUNTDOWN_HAS_BEGUN && endTime <= sessionTime.getValue())
		        		    strlaptime = "00:00:00";
		        		else
		        		    strlaptime = TimingUtil.getTimeAsString(endTime - sessionTime.getValue(), true, false);
		        	
		        
		        }
	    		
	    	
	    	}
        
        strLaptime.update( strlaptime );
        
        if ( needsCompleteRedraw || ( clock.c() && strLaptime.hasChanged() ) )
        {
            if(SCState.getValue() == YellowFlagState.RESUME && !sectorYellowFlag.getValue())
                drawnFontColor = GflagFontColor;
            else
                drawnFontColor = TimeFontColor;
            
            if(strInfo == "SC")
                InfoFontColor = TimeFontColor;
            else
                InfoFontColor = GflagFontColor;
            
            dsSession.draw( offsetX, offsetY, strlaptime,drawnFontColor.getColor(), texture );
            dsInfo.draw( offsetX, offsetY, strInfo, InfoFontColor.getColor(),texture );
        }   
    	
    }
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( timeFont, "timeFont" );
        writer.writeProperty( timeFont2, "timeFont" );
        writer.writeProperty( TimeFontColor, "Time Font Color" );
        writer.writeProperty( YflagFontColor, "Yel flag Font Color" );
        writer.writeProperty( GflagFontColor, "Yel flag Font Color" );
        writer.writeProperty( strPractice1, "" );
        writer.writeProperty( strPractice2, "" );
        writer.writeProperty( strPractice3, "" );
        writer.writeProperty( strPractice4, "" );
        writer.writeProperty( strQualif, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( timeFont ) );
        if ( loader.loadProperty( timeFont2 ) );
        if ( loader.loadProperty( TimeFontColor ) );
        if ( loader.loadProperty( YflagFontColor ) );
        if ( loader.loadProperty( GflagFontColor ) );
        if ( loader.loadProperty( strPractice1 ) );
        if ( loader.loadProperty( strPractice2 ) );
        if ( loader.loadProperty( strPractice3 ) );
        if ( loader.loadProperty( strPractice4 ) );
        if ( loader.loadProperty( strQualif ) );
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Font" );
        propsCont.addProperty( timeFont );
        propsCont.addProperty( timeFont2 );
        propsCont.addProperty( TimeFontColor );
        propsCont.addProperty( YflagFontColor );
        propsCont.addProperty( GflagFontColor );
        propsCont.addGroup( "Session Names" );
        propsCont.addProperty( strPractice1 );
        propsCont.addProperty( strPractice2 );
        propsCont.addProperty( strPractice3 );
        propsCont.addProperty( strPractice4 );
        propsCont.addProperty( strQualif );
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
    
    public SessionTimerWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 15.0f, 5.0f );
        //getBackgroundProperty().setImageValue( "prunn/f109/laptime.png" );
        getBorderProperty().setBorder( null );
    }
}
