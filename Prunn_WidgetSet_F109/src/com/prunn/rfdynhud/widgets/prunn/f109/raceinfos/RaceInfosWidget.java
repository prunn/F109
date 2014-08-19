package com.prunn.rfdynhud.widgets.prunn.f109.raceinfos;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;

import net.ctdp.rfdynhud.gamedata.Laptime;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.util.NumberUtil;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class RaceInfosWidget extends Widget
{
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    
    private DrawnString dsPos = null;
    //private DrawnString dsNumber = null;
    private DrawnString dsName = null;
    private DrawnString dsTeam = null;
    private DrawnString dsTime = null;
    private DrawnString dsTitle = null;
    private DrawnString dsTitle2 = null;
    private DrawnString dsWinner = null;
    
    private final ImagePropertyWithTexture imgPosBig = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos.png" );
    private final ImagePropertyWithTexture imgPosBig1 = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos1.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgNameW = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgTeam", "prunn/f109/team.png" );
    private final ImagePropertyWithTexture imgTeamF = new ImagePropertyWithTexture( "imgTeam", "prunn/f109/team.png" );
    private final ImagePropertyWithTexture imgTeamW = new ImagePropertyWithTexture( "imgTeam", "prunn/f109/team.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f109/qlaptime.png" );
    private final ImagePropertyWithTexture imgTitle = new ImagePropertyWithTexture( "imgTitle", "prunn/f109/title.png" );
    private final ImagePropertyWithTexture imgTitleW = new ImagePropertyWithTexture( "imgTitleW", "prunn/f109/winner.png" );
    private final ImagePropertyWithTexture imgTitleF = new ImagePropertyWithTexture( "imgTitle", "prunn/f109/gapgreen.png" );
    
    
    private final FontProperty posFont = new FontProperty("positionFont", "posFont");
    protected final FontProperty wsbrFont = new FontProperty("Main Font", "wsbrFont");
    protected final ColorProperty BlackFontColor = new ColorProperty("Black Font Color", "BlackFontColor");
    protected final ColorProperty WhiteFontColor = new ColorProperty("White Font Color", "WhiteFontColor");
    protected final BooleanProperty showwinner = new BooleanProperty("Show Winner", "showwinner", true);
    protected final BooleanProperty showfastest = new BooleanProperty("Show Fastest Lap", "showfastest", true);
    protected final BooleanProperty showpitstop = new BooleanProperty("Show Pitstop", "showpitstop", true);
    protected final BooleanProperty showinfo = new BooleanProperty("Show Info", "showinfo", true);
    
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    private float timestamp = -1;
    private float endtimestamp = -1;
    private float pittime = -1;
    private BoolValue pitting = new BoolValue(false);
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private IntValue cveh = new IntValue();
    private IntValue speed = new IntValue();
    private long visibleEndW;
    private long visibleEndF;
    private final FloatValue racetime = new FloatValue( -1f, 0.1f );
    private float sessionstart = 0;
    private BoolValue racefinished = new BoolValue();
    
    private int widgetpart = 0;//0-info 1-pitstop 2-fastestlap 3-winner
    private final FloatValue FastestLapTime = new FloatValue(-1F, 0.001F);
    StandardTLCGenerator gen = new StandardTLCGenerator();
    
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
        int rowHeight = height / 3;
        int fw2 = Math.round(width * 0.56f);
        int fw3 = width - fw2;
        int fw3b = fw2 - Math.round(width * 0.07f);
        
        imgPosBig.updateSize( rowHeight *2, rowHeight*2, isEditorMode );
        imgPosBig1.updateSize( rowHeight *2, rowHeight*2, isEditorMode );
        imgName.updateSize( fw2, height / 3, isEditorMode );
        imgTeam.updateSize( fw2, rowHeight, isEditorMode );
        imgTeamF.updateSize( fw3, rowHeight, isEditorMode );
        imgTime.updateSize( fw3, rowHeight, isEditorMode );
        imgTitle.updateSize( fw3, rowHeight, isEditorMode );
        imgTitleF.updateSize( fw3, rowHeight, isEditorMode );
        imgTitleW.updateSize( fw3b, rowHeight, isEditorMode );
        imgTeamW.updateSize( fw3b, rowHeight, isEditorMode );
        imgNameW.updateSize( fw3b, rowHeight, isEditorMode );
        
        int top1 = ( rowHeight - fh ) / 2;
        int top2 = ( rowHeight - fh ) / 2 + rowHeight;
        int top3 = ( rowHeight - fh ) / 2 + rowHeight*2;
        
        dsPos = drawnStringFactory.newDrawnString( "dsPos", rowHeight + fw2, top2, Alignment.CENTER, false, posFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsName = drawnStringFactory.newDrawnString( "dsName", 10, top2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsTeam = drawnStringFactory.newDrawnString( "dsTeam", 10, top3, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", fw2 + fw3*11/12, top2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsTitle = drawnStringFactory.newDrawnString( "dsTitle", fw2 + fw3*11/12, top3, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsTitle2 = drawnStringFactory.newDrawnString( "dsTitle2", fw2 + fw3*11/12, top1, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsWinner = drawnStringFactory.newDrawnString( "dsWinner", 10, top1, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
            
        cveh.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getDriverId());
        pitting.update(scoringInfo.getViewedVehicleScoringInfo().isInPits());
        //fastest lap
        Laptime lt = scoringInfo.getFastestLaptime();
        
        if(lt == null || !lt.isFinished())
            FastestLapTime.update(-1F);
        else
            FastestLapTime.update(lt.getLapTime());
        //winner part
        if(gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapsCompleted() < 1)
            sessionstart = gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapStartTime();
        if(scoringInfo.getSessionTime() > 0)
            racetime.update( scoringInfo.getSessionTime() - sessionstart );
        
        racefinished.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getFinishStatus().isFinished());
        
               
        //carinfo
        if(cveh.hasChanged() && cveh.isValid() && showinfo.getValue() && !isEditorMode)
        {
            forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            widgetpart = 0;
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd )
        {
            forceCompleteRedraw(true);
            isvisible = true;
            widgetpart = 0;
            return true;
        }
        
        //pitstop   
        if( pitting.hasChanged())
        {
            endtimestamp = 0;
            timestamp = 0;
        }
            
        if( pitting.getValue() && showpitstop.getValue() )
        {
            if(scoringInfo.getViewedVehicleScoringInfo().getStintLength() >= 0.6)
                widgetpart = 1;
            else
                widgetpart = 0;
            
            speed.update( (int)scoringInfo.getViewedVehicleScoringInfo().getScalarVelocity());
            if(speed.hasChanged() && speed.getValue() < 1)
            {
                endtimestamp = gameData.getScoringInfo().getSessionTime();
                timestamp = gameData.getScoringInfo().getSessionTime();
            }
            forceCompleteRedraw(true);
            isvisible = true;
            return true;
        }
        
        //fastest lap
        if(scoringInfo.getSessionNanos() < visibleEndF && FastestLapTime.isValid())
        {
            isvisible = true;
            widgetpart = 2;
            return true; 
        }
        if(FastestLapTime.hasChanged() && FastestLapTime.isValid() && scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() > 1 && showfastest.getValue())
        {
            forceCompleteRedraw(true);
            visibleEndF = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            widgetpart = 2;
            return true;
        }
        
        //winner part
        if(scoringInfo.getSessionNanos() < visibleEndW )
        {
            isvisible = true;
            widgetpart = 3;
            return true;
        }
         
        if(racefinished.hasChanged() && racefinished.getValue() && showwinner.getValue() )
        {
            forceCompleteRedraw(true);
            visibleEndW = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            widgetpart = 3;
            return true;
        }
        isvisible = false;
        return false;	
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        int rowHeight = height / 3;
        if(isEditorMode)
            widgetpart = 2;
        switch(widgetpart)
        {
            case 1: //Pit Stop
                    texture.clear( imgName.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    texture.clear( imgTime.getTexture(), offsetX + imgTeam.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    break;
        
            case 2: //Fastest Lap
                    texture.clear( imgName.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    texture.clear( imgTeam.getTexture(), offsetX, offsetY + rowHeight + height / 3, false, null );
                    texture.clear( imgTeamF.getTexture(), offsetX + imgTeam.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    texture.clear( imgTime.getTexture(), offsetX + imgTeam.getTexture().getWidth(), offsetY + rowHeight*2, false, null );
                    break;
                    
            case 3: //Winner
                    texture.clear( imgNameW.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    texture.clear( imgTeamW.getTexture(), offsetX, offsetY + rowHeight + height / 3, false, null );
                    texture.clear( imgTime.getTexture(), offsetX + imgTeam.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    texture.clear( imgTitleW.getTexture(), offsetX, offsetY, false, null );
                    texture.clear( imgTime.getTexture(), offsetX + imgTeam.getTexture().getWidth(), offsetY + rowHeight*2, false, null );
                    texture.clear( imgTime.getTexture(), offsetX + imgTeam.getTexture().getWidth(), offsetY, false, null );
                    break;
            
            default: //Info
                    texture.clear( imgName.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    texture.clear( imgTeam.getTexture(), offsetX, offsetY + rowHeight + height / 3, false, null );
                    
                    if(gameData.getScoringInfo().getViewedVehicleScoringInfo().getNextInFront( false ) == null)
                        texture.clear( imgPosBig1.getTexture(), offsetX + imgName.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    else
                        texture.clear( imgPosBig.getTexture(), offsetX + imgName.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    break;
        }
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
    	sessionTime.update(scoringInfo.getSessionTime());
    	if(isEditorMode)
            widgetpart = 2;
    	if ( needsCompleteRedraw || sessionTime.hasChanged() || FastestLapTime.hasChanged())
        {
    	    String team, name, title, title2,pos,time,winner;
            
    	    switch(widgetpart)
            {
                case 1: //Pit Stop
                        VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
                        
                        //number = "";
                        team = "";
                        
                        if(currentcarinfos.getNumOutstandingPenalties() > 0)
                            title="";
                        else
                            title="";
                        
                        if(scoringInfo.getViewedVehicleScoringInfo().getScalarVelocity() < 1)
                        {
                           endtimestamp = gameData.getScoringInfo().getSessionTime();
                           pittime = endtimestamp - timestamp;
                        }
                        else
                            if(scoringInfo.getViewedVehicleScoringInfo().getScalarVelocity() > 1 || pittime > 0)
                                pittime = endtimestamp - timestamp;
                            else
                                pittime = 0;
                        
                        name = currentcarinfos.getDriverNameShort();
                        pos = "";
                        //pos = NumberUtil.formatFloat( currentcarinfos.getPlace(getConfiguration().getUseClassScoring()), 0, true);
                        time = TimingUtil.getTimeAsString(pittime, false, false, true, false );
                        winner = "";
                        title2="";
                        break;
                    
                case 2: //Fastest Lap
                        VehicleScoringInfo fastcarinfos = gameData.getScoringInfo().getFastestLapVSI();
                        
                        if(fastcarinfos.getVehicleInfo() != null)
                        {
                            team = gen.generateShortTeamNames( fastcarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() );
                            //number = NumberUtil.formatFloat( fastcarinfos.getVehicleInfo().getCarNumber(), 0, true);
                        }
                        else
                        {
                            team = fastcarinfos.getVehicleClass(); 
                            //number = NumberUtil.formatFloat( fastcarinfos.getDriverID(), 0, true);
                        }
                        pos = "";
                        name = fastcarinfos.getDriverNameShort();
                        //pos = NumberUtil.formatFloat( fastcarinfos.getPlace(getConfiguration().getUseClassScoring()), 0, true);
                        title = TimingUtil.getTimeAsLaptimeString(FastestLapTime.getValue() );
                        time = "Fastest Lap";
                        winner = "";
                        title2="";
                        break;
                        
                case 3: //Winner
                        VehicleScoringInfo winnercarinfos = gameData.getScoringInfo().getLeadersVehicleScoringInfo();
                        
                        if(winnercarinfos.getVehicleInfo() != null)
                        {
                            team = gen.generateShortTeamNames( winnercarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() );
                            //number = NumberUtil.formatFloat( winnercarinfos.getVehicleInfo().getCarNumber(), 0, true);
                        }
                        else
                        {
                            team = winnercarinfos.getVehicleClass(); 
                            //number = NumberUtil.formatFloat( winnercarinfos.getDriverID(), 0, true);
                        }
                        float laps=0;
                        
                        for(int i=1;i <= winnercarinfos.getLapsCompleted(); i++)
                        {
                            if(winnercarinfos.getLaptime(i) != null)
                                laps = winnercarinfos.getLaptime(i).getLapTime() + laps;
                            else
                            {
                                laps = racetime.getValue();
                                i = winnercarinfos.getLapsCompleted()+1;
                            }
                        } 
                        pos = "";
                        winner = "Winner";
                        name = winnercarinfos.getDriverNameShort();
                        title2= TimingUtil.getTimeAsLaptimeString( laps );
                        title = NumberUtil.formatFloat( gameData.getScoringInfo().getLeadersVehicleScoringInfo().getTopspeed(), 3, false ) + " km/h";
                        time = NumberUtil.formatFloat( gameData.getTrackInfo().getTrack().getTrackLength() * gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapsCompleted() / 1000f, 3, true ) + " Km";
                        break;
                
                default: //Info
                        VehicleScoringInfo currentcarinfosInfo = gameData.getScoringInfo().getViewedVehicleScoringInfo();
                        
                        if(currentcarinfosInfo.getVehicleInfo() != null)
                            team = gen.generateShortTeamNames( currentcarinfosInfo.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() );
                          
                        else
                            team = currentcarinfosInfo.getVehicleClass(); 
                        
                        time="";
                        title="";
                        title2="";
                        name = currentcarinfosInfo.getDriverNameShort();
                        pos = Integer.toString( currentcarinfosInfo.getPlace(false) );
                        winner = "";
                        break;
            }
            
        	dsPos.draw( offsetX, offsetY, pos, texture );
            dsName.draw( offsetX, offsetY, name, texture );
            dsTeam.draw( offsetX, offsetY, team, texture );
            dsWinner.draw( offsetX, offsetY, winner, texture );
            dsTime.draw( offsetX, offsetY, time, texture);
            dsTitle.draw( offsetX, offsetY, title, texture );
            dsTitle2.draw( offsetX, offsetY, title2, texture );
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
        writer.writeProperty(showwinner, "");
        writer.writeProperty(showfastest, "");
        writer.writeProperty(showpitstop, "");
        writer.writeProperty(showinfo, "");
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
        if ( loader.loadProperty( showwinner ) );
        if ( loader.loadProperty( showfastest ) );
        if ( loader.loadProperty( showpitstop ) );
        if ( loader.loadProperty( showinfo ) );
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
        propsCont.addProperty(showwinner);
        propsCont.addProperty(showfastest);
        propsCont.addProperty(showpitstop);
        propsCont.addProperty(showinfo);
        
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
    
    public RaceInfosWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 50.0f, 20.5f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0;
        getBackgroundProperty().setColorValue( "#00000000" );
    }
    
}
