package uk.co.healtht.healthtouch.ui.widget;

import java.util.List;

/**
 * Created by andyj on 27/11/2017.
 */

public class GraphValuePointsDataSet {

    public List<GraphValuePoint> graphValuePoints;
    public String dataSetLabel = "";
    public int dataSetColor = Color_Default;

    // colors 0x + AA + RR + GG + BB
    // first 2 chars set transparency
    // second 2 set red , 3rd set green, 4th set blue
    public static final int Color_Default = 0xFF666666;  // a sort of mild black
    public static final int Color_Red = 0xFFFF0000;
    public static final int Color_Green = 0xFF00FF00;
    public static final int Color_Yellow = 0xFF00FFFF;
    public static final int Color_Blue = 0xFF0000FF;
}
