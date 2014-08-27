package com.prunn.rfdynhud.widgets.prunn.f109.racecontrol;

import java.awt.Font;
import java.io.IOException;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetF109;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
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
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.EnumValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * 
 * 
 * @author Prunn 2011
 */


public class RaceControlWidget extends Widget
{
    private DrawnString dsRC = null;
    private DrawnString dsMessage = null;
    private final EnumValue<YellowFlagState> SCState = new EnumValue<YellowFlagState>();
    private int widgetpart = 0;
    private IntValue Penalties[];
    private IntValue Pentotal =  new IntValue();
    private int flaggeddriver = 0;
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/f109/name.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f109/team.png" );
    
    protected final FontProperty wsbrFont = new FontProperty("Main Font", "wsbrFont");
    protected final ColorProperty BlackFontColor = new ColorProperty("Black Font Color", "BlackFontColor");
    protected final ColorProperty WhiteFontColor = new ColorProperty("White Font Color", "WhiteFontColor");
    private final DelayProperty visibleTime;
    private long visibleEnd;
    
    
    
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
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int numveh = gameData.getScoringInfo().getNumVehicles();
        
        imgName.updateSize( Math.round(width * 0.25f), height / 2, isEditorMode );
        imgTime.updateSize( width, height / 2, isEditorMode );
        
        dsRC = drawnStringFactory.newDrawnString( "dsRC", 10, fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), BlackFontColor.getColor(), null, "" );
        dsMessage = drawnStringFactory.newDrawnString( "dsMessage", 10, height/2 + fh/2, Alignment.LEFT, false, wsbrFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        Penalties = new IntValue[numveh];
        for(int i=0;i < numveh;i++)
        { 
            Penalties[i] = new IntValue();
            Penalties[i].update(0);
            Penalties[i].setUnchanged();
        }
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        int numveh = gameData.getScoringInfo().getNumVehicles();
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        SCState.update(gameData.getScoringInfo().getYellowFlagState());
        
        if(isEditorMode)
            return true;
        
        if(scoringInfo.getSessionNanos() < visibleEnd)
            return true;
        
        if(SCState.hasChanged() && (SCState.getValue() == YellowFlagState.PENDING || SCState.getValue() == YellowFlagState.LAST_LAP))
        {
            widgetpart = 1;
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            return true;
        }
        
        if(scoringInfo.getSessionType().isRace())
        {
            
            int total=0;
            for(int j=0;j < numveh;j++)
            {
                total += scoringInfo.getVehicleScoringInfo( j ).getNumOutstandingPenalties();
            }
            Pentotal.update( total );
           
            if(Pentotal.getValue() > Pentotal.getOldValue() && Pentotal.hasChanged() && Pentotal.getValue() > 0)
            {
               widgetpart = 0;
               visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
               return true;
            }
            else
                Pentotal.hasChanged();
        }
        
        return false;   
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        texture.clear( imgName.getTexture(), offsetX, offsetY, false, null );
        texture.clear( imgTime.getTexture(), offsetX, offsetY + height/2, false, null );
     
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        int numveh = gameData.getScoringInfo().getNumVehicles();
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if(widgetpart == 1)
        {
            
            if ( needsCompleteRedraw || SCState.getValue() == YellowFlagState.PENDING)
                dsMessage.draw( offsetX, offsetY, "SAFETY CAR DEPLOYED", texture );
            else
                if ( needsCompleteRedraw || SCState.getValue() == YellowFlagState.LAST_LAP)
                    dsMessage.draw( offsetX, offsetY, "SAFETY CAR IN THIS LAP", texture );
               
            dsRC.draw( offsetX, offsetY, "Race Control", texture );
        }
        else
            {
                for(int i=0;i < numveh;i++)
                {
                   Penalties[i].update( scoringInfo.getVehicleScoringInfo( i ).getNumOutstandingPenalties() );
                                
                   if(Penalties[i].hasChanged() && Penalties[i].getValue() > 0 )
                       flaggeddriver = i;
                }
                VehicleScoringInfo vsi = gameData.getScoringInfo().getVehicleScoringInfo( flaggeddriver );
                
                if ( needsCompleteRedraw )
                {
                    dsRC.draw( offsetX, offsetY, "Race Control", texture );
                    dsMessage.draw( offsetX, offsetY, "Drive Through Penalty for " + vsi.getDriverName(), texture );
                }
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
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( wsbrFont ) );
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
    
    public RaceControlWidget()
    {
        super( PrunnWidgetSetF109.INSTANCE, PrunnWidgetSetF109.WIDGET_PACKAGE_F109, 55.0f, 10.5f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0;
        Penalties = null;
    }
    
}
