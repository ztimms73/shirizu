package org.xtimms.tokusho.ui.harmonize.blend;

import org.xtimms.tokusho.ui.harmonize.hct.Cam16;
import org.xtimms.tokusho.ui.harmonize.hct.Hct;
import org.xtimms.tokusho.ui.harmonize.utils.ColorUtils;
import org.xtimms.tokusho.ui.harmonize.utils.MathUtils;

/** Functions for blending in HCT and CAM16. */
public class Blend {
    private Blend() {}

    /**
     * Blend the design color's HCT hue towards the key color's HCT hue, in a way that leaves the
     * original color recognizable and recognizably shifted towards the key color.
     *
     * @param designColor ARGB representation of an arbitrary color.
     * @param sourceColor ARGB representation of the main theme color.
     * @return The design color with a hue shifted towards the system's color, a slightly
     *     warmer/cooler variant of the design color's hue.
     */
    public static int harmonize(int designColor, int sourceColor) {
        Hct fromHct = Hct.fromInt(designColor);
        Hct toHct = Hct.fromInt(sourceColor);
        double differenceDegrees = MathUtils.differenceDegrees(fromHct.getHue(), toHct.getHue());
        double rotationDegrees = Math.min(differenceDegrees * 0.5, 15.0);
        double outputHue =
                MathUtils.sanitizeDegreesDouble(
                        fromHct.getHue()
                                + rotationDegrees * MathUtils.rotationDirection(fromHct.getHue(), toHct.getHue()));
        return Hct.from(outputHue, fromHct.getChroma(), fromHct.getTone()).toInt();
    }

    /**
     * Blends hue from one color into another. The chroma and tone of the original color are
     * maintained.
     *
     * @param from ARGB representation of color
     * @param to ARGB representation of color
     * @param amount how much blending to perform; 0.0 >= and <= 1.0
     * @return from, with a hue blended towards to. Chroma and tone are constant.
     */
    public static int hctHue(int from, int to, double amount) {
        int ucs = cam16Ucs(from, to, amount);
        Cam16 ucsCam = Cam16.fromInt(ucs);
        Cam16 fromCam = Cam16.fromInt(from);
        Hct blended = Hct.from(ucsCam.getHue(), fromCam.getChroma(), ColorUtils.lstarFromArgb(from));
        return blended.toInt();
    }

    /**
     * Blend in CAM16-UCS space.
     *
     * @param from ARGB representation of color
     * @param to ARGB representation of color
     * @param amount how much blending to perform; 0.0 >= and <= 1.0
     * @return from, blended towards to. Hue, chroma, and tone will change.
     */
    public static int cam16Ucs(int from, int to, double amount) {
        Cam16 fromCam = Cam16.fromInt(from);
        Cam16 toCam = Cam16.fromInt(to);
        double fromJ = fromCam.getJstar();
        double fromA = fromCam.getAstar();
        double fromB = fromCam.getBstar();
        double toJ = toCam.getJstar();
        double toA = toCam.getAstar();
        double toB = toCam.getBstar();
        double jstar = fromJ + (toJ - fromJ) * amount;
        double astar = fromA + (toA - fromA) * amount;
        double bstar = fromB + (toB - fromB) * amount;
        return Cam16.fromUcs(jstar, astar, bstar).toInt();
    }
}
