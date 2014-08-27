package com.prunn.rfdynhud.widgets.prunn.f109.qualifinfos;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;
import com.prunn.rfdynhud.widgets.prunn.f109.qtime.QualTimeWidget;

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
import net.ctdp.rfdynhud.util.NumberUtil;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class QualifInfosWidget extends Widget
{
    
    
    private DrawnString dsPos = null;
    private DrawnString dsName = null;
    private DrawnString dsTeam = null;
    private DrawnString dsTime = null;
    private DrawnString dsGap = null;
    
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos1.png" );
    private final ImagePropertyWithTexture imgPosOut = new ImagePropertyWithTexture( "imgPosOut", "prunn/f109/posknockout.png" );
    //private final ImagePropertyWithTexture imgNumber = new ImagePropertyWithTexture( "imgNumber", "prunn/f109/number.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgTeam", "prunn/f109/team.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f109/qlaptime.png" );
    
    
    protected final FontProperty wsbrFont = new FontProperty("Main Font", "wsbrFont");
    protected final ColorProperty BlackFontColor = new ColorProperty("Black Font Color", "BlackFontColor");
    protected final ColorProperty WhiteFontColor = new ColorProperty("White Font Color", "WhiteFontColor");
    private IntProperty knockout = new IntProperty("Knockout position", 10);
    protected final ColorProperty KnockoutFontColor = new ColorProperty("Knockout Font Color", "KnockoutFontColor");
    
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private IntValue cveh = new IntValue();
    
    @Override
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
        if(name.equals("KnockoutFontColor"))
            return "#DA1C19";
        
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
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
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
        int rowHeight = height / 2;
        int fh = TextureImage2D.getStringHeight( "0", getFontProperty() );
        
        int fieldWidth1 = rowHeight;
        int fieldWidth2 = ( width / 2 ) + fieldWidth1 / 2;
        int fieldWidth3 = width - fieldWidth1 - fieldWidth2;
        
        imgPos.updateSize( rowHeight, rowHeight, isEditorMode );
        imgPosFirst.updateSize( rowHeight, rowHeight, isEditorMode );
        imgPosOut.updateSize( rowHeight, rowHeight, isEditorMode );
        //imgNumber.updateSize( height / 3, height / 3, isEditorMode );
        imgName.updateSize(fieldWidth2, rowHeight, isEditorMode );
        imgTeam.updateSize( fieldWidth2, rowHeight, isEditorMode );
        imgTime.updateSize( fieldWidth3, rowHeight, isEditorMode );
        
        int top1 = ( rowHeight - fh ) / 2;
        int top2 = rowHeight + ( rowHeight - fh ) / 2;
        dsPos = drawnStringFactory.newDrawnString( "dsPos", fieldWidth1 / 2, top1, Alignment.CENTER, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor() );
        dsName = drawnStringFactory.newDrawnString( "dsName", fieldWidth1 + 10, top1, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor() );
        dsTeam = drawnStringFactory.newDrawnString( "dsTeam", fieldWidth1 + 10, top2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor() );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", fieldWidth1 + fieldWidth2 + fieldWidth3 * 10 / 12, top1, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor() );
        dsGap = drawnStringFactory.newDrawnString( "dsGap", fieldWidth1 + fieldWidth2 + fieldWidth3 * 10 / 12, top2, Alignment.RIGHT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor() );
        
    }
    
    @Override
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
            
        cveh.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getDriverId());
        
        if(isEditorMode)
            return true;
        
        
        if(QualTimeWidget.visible())
            return false;
        
        //carinfo
        
        if(cveh.hasChanged() && cveh.isValid())
        {
            forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd )
        {
            forceCompleteRedraw(true);
            return true;
        }
        
        //pitstop   
        if( scoringInfo.getViewedVehicleScoringInfo().isInPits() )
        {
            forceCompleteRedraw(true);
            return true;
        }
        
        return false;	
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
        
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY, false, null );
        texture.clear( imgTeam.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY + height / 2, false, null );
        
        if(currentcarinfos.getBestLapTime() > 0)
        {        
            if(currentcarinfos.getPlace( false ) == 1)
                texture.clear( imgPosFirst.getTexture(), offsetX, offsetY, false, null );
            else
                if(currentcarinfos.getPlace( false ) <= knockout.getValue())
                    texture.clear( imgPos.getTexture(), offsetX, offsetY, false, null );
                else
                    texture.clear( imgPosOut.getTexture(), offsetX, offsetY, false, null );
        
            if(currentcarinfos.getPlace( false ) > 1)
            { 
                texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgTeam.getTexture().getWidth(), offsetY, false, null );
                texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgTeam.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth(), false, null );
            }
            else
                texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgTeam.getTexture().getWidth(), offsetY + imgPos.getTexture().getWidth(), false, null );
        }       
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
    	sessionTime.update(scoringInfo.getSessionTime());
    	
    	if ( needsCompleteRedraw || sessionTime.hasChanged())
        {
    	    String team, name, pos,gap,time;
            VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
            
        	name = currentcarinfos.getDriverName();
            pos = NumberUtil.formatFloat( currentcarinfos.getPlace(false), 0, true);
            
            if(currentcarinfos.getVehicleInfo() != null)
            {
                team = PrunnWidgetSetF109.generateShortTeamNames( currentcarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() );
            }
            else
            {
                team = currentcarinfos.getVehicleClass(); 
            }    
        	
            
            if(currentcarinfos.getBestLapTime() > 0)
            {
                if(currentcarinfos.getPlace( false ) > 1)
                { 
                    time = TimingUtil.getTimeAsLaptimeString(currentcarinfos.getBestLapTime() );
                    gap = "+ " +  TimingUtil.getTimeAsLaptimeString( currentcarinfos.getBestLapTime() - gameData.getScoringInfo().getLeadersVehicleScoringInfo().getBestLapTime() );
                }
                else
                {
                    time = "";
                    gap = TimingUtil.getTimeAsLaptimeString(currentcarinfos.getBestLapTime());
                }
                    
            }
            else
            {
                time="";
                gap="";
            }
            if(currentcarinfos.getBestLapTime() > 0)
                dsPos.draw( offsetX, offsetY, pos,( currentcarinfos.getPlace(false) <= knockout.getValue() ) ? WhiteFontColor.getColor() : KnockoutFontColor.getColor(), texture );
            dsName.draw( offsetX, offsetY, name, texture );
            dsTeam.draw( offsetX, offsetY, team, texture );
            dsTime.draw( offsetX, offsetY, time, texture);
            dsGap.draw( offsetX, offsetY, gap, texture );
        }
         
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( wsbrFont, "" );
        writer.writeProperty( BlackFontColor, "" );
        writer.writeProperty( WhiteFontColor, "" );
        writer.writeProperty( KnockoutFontColor, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty( knockout, "" );
        
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( wsbrFont ) );
        if ( loader.loadProperty( BlackFontColor ) );
        if ( loader.loadProperty( WhiteFontColor ) );
        if ( loader.loadProperty( KnockoutFontColor ) );
        if(!loader.loadProperty(visibleTime));
        if ( loader.loadProperty( knockout ) );
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( wsbrFont );
        propsCont.addProperty( BlackFontColor );
        propsCont.addProperty( WhiteFontColor );
        propsCont.addProperty( KnockoutFontColor );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty( knockout );
        
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
    
    public QualifInfosWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 50.0f, 20.5f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0x8000000000000000L;
        getBackgroundProperty().setColorValue( "#00000000" );
    }
    
}
