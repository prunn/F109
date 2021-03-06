package com.prunn.rfdynhud.widgets.prunn.f109.qualtimingtower;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.StandingsTools;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StandingsView;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class QualifTimingTowerWidget extends Widget
{
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/f109/pos1.png" );
    private final ImagePropertyWithTexture imgPosOut = new ImagePropertyWithTexture( "imgPosOut", "prunn/f109/posknockout.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f109/qlaptime.png" );
    private final ImagePropertyWithTexture imgTimeGreen = new ImagePropertyWithTexture( "imgTime", "prunn/f109/gapgreen.png" );
    
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetF109.FONT_COLOR2_NAME );
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 8 );
    
    private final DelayProperty visibleTime = new DelayProperty( "visibleTime", DelayProperty.DisplayUnits.SECONDS, 2 );
    private long visibleEnd = -1L;
    private long[] visibleEndArray;
    
    private DrawnString[] dsPos = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    protected final ColorProperty KnockoutFontColor = new ColorProperty("Knockout Font Color", PrunnWidgetSetF109.KNOCKOUT_FONT_COLOR);
    private int knockout;
    private IntProperty knockoutQual = new IntProperty("Knockout position Qual", 10);
    private IntProperty knockoutFP1 = new IntProperty("Knockout position FP1", 16);
    private IntProperty knockoutFP2 = new IntProperty("Knockout position FP2", 16);
    private IntProperty knockoutFP3 = new IntProperty("Knockout position FP3", 16);
    private IntProperty knockoutFP4 = new IntProperty("Knockout position FP4", 16);
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    
    private VehicleScoringInfo[] vehicleScoringInfos;
    private IntValue[] positions = null;
    private StringValue[] driverNames = null;
    private FloatValue[] gaps = null;
    private int[] driverIDs = null;
    private boolean[] gapFlag = null;
    private boolean[] gapFlag2 = null;
    private final IntValue numValid = new IntValue();
    
    
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        visibleEnd = -1L;
        numValid.reset();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
    }
    
    private void initValues()
    {
        int maxNumItems = numVeh.getValue();
        
        if ( ( positions != null ) && ( positions.length == maxNumItems ) )
            return;
        
        gaps = new FloatValue[maxNumItems];
        gapFlag = new boolean[maxNumItems];
        gapFlag2 = new boolean[maxNumItems];
        positions = new IntValue[maxNumItems];
        driverNames = new StringValue[maxNumItems];
        driverIDs = new int[maxNumItems];
        visibleEndArray = new long[maxNumItems];
        vehicleScoringInfos = new VehicleScoringInfo[maxNumItems];
        
        for(int i=0;i < maxNumItems;i++)
        { 
            positions[i] = new IntValue();
            driverNames[i] = new StringValue();
            gaps[i] = new FloatValue();
        }
        
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int maxNumItems = numVeh.getValue();
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / maxNumItems;
        int fieldWidth1 = rowHeight;
        int fieldWidth2 = Math.round(width * 0.28f);
        int fieldWidth3 = width - fieldWidth1 - fieldWidth2;
        
        imgPos.updateSize( fieldWidth1, rowHeight, isEditorMode );
        imgPosFirst.updateSize( fieldWidth1, rowHeight, isEditorMode );
        imgPosOut.updateSize( fieldWidth1, rowHeight, isEditorMode );
        imgName.updateSize( fieldWidth2, rowHeight, isEditorMode );
        imgTime.updateSize( fieldWidth3, rowHeight, isEditorMode );
        imgTimeGreen.updateSize( fieldWidth3, rowHeight, isEditorMode );
        
        Color blackFontColor = getFontColor();
        Color whiteFontColor = fontColor2.getColor();
        
        dsPos = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
        
        int top = ( rowHeight - fh ) / 2;
        
        for(int i=0;i < maxNumItems;i++)
        {
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", fieldWidth1 / 2 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", fieldWidth1 + 10 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), blackFontColor );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", fieldWidth1 + fieldWidth2 + fieldWidth3 * 5 / 6 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor );
            top += rowHeight;
        }
        
        switch(gameData.getScoringInfo().getSessionType())
        {
            case QUALIFYING1:
                knockout = knockoutQual.getValue();
                break;
            case PRACTICE1:
                knockout = knockoutFP1.getValue();
                break;
            case PRACTICE2:
                knockout = knockoutFP2.getValue();
                break;
            case PRACTICE3:
                knockout = knockoutFP3.getValue();
                break;
            case PRACTICE4:
                knockout = knockoutFP4.getValue();
                break;
            default:
                knockout = 100;
                break;
        }
        if(isEditorMode)
            knockout = knockoutQual.getValue();
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        
        initValues();
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        
        VehicleScoringInfo  comparedVSI = gameData.getScoringInfo().getViewedVehicleScoringInfo();
        if(scoringInfo.getViewedVehicleScoringInfo().getBestLapTime() > 0)
        {
            if(gameData.getScoringInfo().getViewedVehicleScoringInfo().getPlace( false ) > drawncars)
                comparedVSI = gameData.getScoringInfo().getVehicleScoringInfo( gameData.getScoringInfo().getViewedVehicleScoringInfo().getPlace( false ) - 5 );
            else
                comparedVSI = gameData.getScoringInfo().getViewedVehicleScoringInfo();
        
        }
        else
        {
            for(int i=drawncars-1; i >= 0; i--)
            {
                if(scoringInfo.getVehicleScoringInfo( i ).getBestLapTime() > 0)
                {
                    comparedVSI = scoringInfo.getVehicleScoringInfo( i ); 
                    break;
                }
            }
        }

        StandingsTools.getDisplayedVSIsForScoring(scoringInfo, comparedVSI, false, StandingsView.RELATIVE_TO_LEADER, true, vehicleScoringInfos);
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = vehicleScoringInfos[i];
            
            if(vsi != null)
            {

                positions[i].update( vsi.getPlace( false ) );
                driverNames[i].update(PrunnWidgetSetF109.generateThreeLetterCode( vsi.getDriverName() ));
                //logCS( vsi.getDriverName(), driverNames[i].getValue() );
                //driverNames[i].update(vsi.getDriverNameTLC( true ));
                gaps[i].setUnchanged();
                gaps[i].update(vsi.getBestLapTime());
                gapFlag[i] = gaps[i].hasChanged( false ) || isEditorMode;
                gapFlag2[i] = gapFlag[i];// || gapFlag2[i];
            }
        }
        
        if((scoringInfo.getSessionNanos() >= visibleEnd) && (visibleEnd != -1L))
        {
            visibleEnd = -1L;
            if ( !isEditorMode )
                forceCompleteRedraw( true );
        }
        
        if(!gaps[0].isValid())
            visibleEnd = -1L;
        else if(gapFlag[0])
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
        
        for(int i=1;i < drawncars;i++)
        {
            if(gaps[i].isValid())
            {
                if(gapFlag[i] && !isEditorMode )
                {
                    //search if the time really changed or just the position before redrawing
                    for(int j=0;j < drawncars; j++)
                    {
                        if ( vehicleScoringInfos[i].getDriverId() == driverIDs[j] )
                        {
                            if(gaps[i].getValue() == gaps[j].getOldValue())
                            {
                                gapFlag[i] = false;
                                break;
                            }
                        }
                    }
                }
                
                if((scoringInfo.getSessionNanos() >= visibleEndArray[i]) && (visibleEndArray[i] != -1L))
                {
                    visibleEndArray[i] = -1L;
                    if ( !isEditorMode )
                        forceCompleteRedraw( true );
                }
                
                if(gapFlag[i]) 
                {
                    visibleEndArray[i] = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
                    if ( !isEditorMode )
                        forceCompleteRedraw( true );
                }
            }
        }
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = vehicleScoringInfos[i];
            
            if(vsi != null)
            {
                driverIDs[i] = vsi.getDriverId();
            }
        }
        
        int nv = 0;
        for(int i=0;i < drawncars;i++)
        {
            if(gaps[i].isValid())
                nv++;
        }
        
        numValid.update( nv );
        if ( numValid.hasChanged() && !isEditorMode )
            forceCompleteRedraw( true );
        
        if( gameData.getScoringInfo().getLeadersVehicleScoringInfo().getBestLapTime() > 0 || isEditorMode)
        {
            return true;
        }
        
        return false;
        
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int maxNumItems = numVeh.getValue();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        
        if(gaps[0].isValid())
        {
            texture.clear( imgPosFirst.getTexture(), offsetX, offsetY, false, null );
            texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY, false, null );
            
            if(scoringInfo.getSessionNanos() < visibleEnd)
                texture.clear( imgTimeGreen.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY, true, null );
            else
                texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY, true, null );
        }
        
        int rowHeight = height / maxNumItems;
        
        for(int i=1;i < drawncars;i++)
        {
            if(gaps[i].isValid())
            {
                if(positions[i].getValue() <= knockout)
                    texture.clear( imgPos.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                else   
                    texture.clear( imgPosOut.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                
                texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY+rowHeight*i, false, null );
                    
                if(scoringInfo.getSessionNanos() < visibleEndArray[i])
                    texture.clear( imgTime.getTexture(), offsetX + imgPos.getTexture().getWidth() + imgName.getTexture().getWidth(), offsetY+rowHeight*i, true, null );
            }
            
        }
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        int drawncars = Math.min( gameData.getScoringInfo().getNumVehicles(), numVeh.getValue() );
        
        //one time for leader
        
        if ( needsCompleteRedraw || ( clock.c() && gapFlag2[0]))
        {
            if(gaps[0].isValid())
            {
                //VehicleScoringInfo vsi = vehicleScoringInfos[0];
                
                dsPos[0].draw( offsetX, offsetY, positions[0].getValueAsString(), texture );
                dsName[0].draw( offsetX, offsetY, driverNames[0].getValue(), texture );
                dsTime[0].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[0].getValue() ) , texture);
            }
            else
            {
                dsTime[0].draw( offsetX, offsetY, "" , texture);
            }
            
            gapFlag2[0] = false;
        }
        
        // the other guys
        for(int i=1;i < drawncars;i++)
        { 
            if ( needsCompleteRedraw || ( clock.c() && gapFlag2[i]))
            {
                if(gaps[i].isValid())
                {
                    dsPos[i].draw( offsetX, offsetY, positions[i].getValueAsString(),( positions[i].getValue() <= knockout ) ? fontColor2.getColor() : KnockoutFontColor.getColor(), texture );
                    dsName[i].draw( offsetX, offsetY,driverNames[i].getValue() , texture );  
                    
                    if(gameData.getScoringInfo().getSessionNanos() < visibleEndArray[i])
                        dsTime[i].draw( offsetX, offsetY,"+ " + TimingUtil.getTimeAsLaptimeString(gaps[i].getValue() - gaps[0].getValue()) , texture);
                    else
                        dsTime[i].draw( offsetX, offsetY,"", texture);
                }
                
                gapFlag2[i] = false;
            }
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( KnockoutFontColor, "" );
        writer.writeProperty( knockoutQual, "" );
        writer.writeProperty( knockoutFP1, "" );
        writer.writeProperty( knockoutFP2, "" );
        writer.writeProperty( knockoutFP3, "" );
        writer.writeProperty( knockoutFP4, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( knockoutQual ) );
        else if ( loader.loadProperty( knockoutFP1 ) );
        else if ( loader.loadProperty( knockoutFP2 ) );
        else if ( loader.loadProperty( knockoutFP3 ) );
        else if ( loader.loadProperty( knockoutFP4 ) );
        else if ( loader.loadProperty( KnockoutFontColor ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        
        propsCont.addProperty( fontColor2 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        
        propsCont.addProperty( numVeh );
        propsCont.addGroup( "Knockout Infos" );
        propsCont.addProperty( knockoutQual );
        propsCont.addProperty( knockoutFP1 );
        propsCont.addProperty( knockoutFP2 );
        propsCont.addProperty( knockoutFP3 );
        propsCont.addProperty( knockoutFP4 );
        propsCont.addProperty( KnockoutFontColor );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxnameoffset );
        propsCont.addProperty( fontxtimeoffset );
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
    
    public QualifTimingTowerWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 20.0f, 32.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetF109.F109_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetF109.FONT_COLOR1_NAME );
    }
}
