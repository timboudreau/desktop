/*
 * The MIT License
 *
 * Copyright 2020 Mastfrog Technologies.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mastfrog.colors.space;

import static com.mastfrog.colors.space.Standard.CIE_1931;
import static com.mastfrog.colors.space.Standard.CIE_1964;
import java.util.Arrays;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Optional;

/**
 * The Illuminant portion of the LAB color standard. Values derived from:
 * <pre>
 * Observer	2° (CIE 1931)	10° (CIE 1964)	Note
 * Illuminant	X2	Y2	Z2	X10	Y10	Z10
 * A	109.850	100.000	35.585	111.144	100.000	35.200	Incandescent/tungsten
 * B	99.0927	100.000	85.313	99.178;	100.000	84.3493	Old direct sunlight at noon
 * C	98.074	100.000	118.232	97.285	100.000	116.145	Old daylight
 * D50	96.422	100.000	82.521	96.720	100.000	81.427	ICC profile PCS
 * D55	95.682	100.000	92.149	95.799	100.000	90.926	Mid-morning daylight
 * D65	95.047	100.000	108.883	94.811	100.000	107.304	Daylight, sRGB, Adobe-RGB
 * D75	94.972	100.000	122.638	94.416	100.000	120.641	North sky daylight
 * E	100.000	100.000	100.000	100.000	100.000	100.000	Equal energy
 * F1	92.834	100.000	103.665	94.791	100.000	103.191	Daylight Fluorescent
 * F2	99.187	100.000	67.395	103.280	100.000	69.026	Cool fluorescent
 * F3	103.754	100.000	49.861	108.968	100.000	51.965	White Fluorescent
 * F4	109.147	100.000	38.813	114.961	100.000	40.963	Warm White Fluorescent
 * F5	90.872	100.000	98.723	93.369	100.000	98.636	Daylight Fluorescent
 * F6	97.309	100.000	60.191	102.148	100.000	62.074	Lite White Fluorescent
 * F7	95.044	100.000	108.755	95.792	100.000	107.687	Daylight fluorescent, D65 simulator
 * F8	96.413	100.000	82.333	97.115	100.000	81.135	Sylvania F40, D50 simulator
 * F9	100.365	100.000	67.868	102.116	100.000	67.826	Cool White Fluorescent
 * F10	96.174	100.000	81.712	99.001	100.000	83.134	Ultralume 50, Philips TL85
 * F11	100.966	100.000	64.370	103.866	100.000	65.627	Ultralume 40, Philips TL84
 * F12	108.046	100.000	39.228	111.428	100.000	40.353	Ultralume 30, Philips TL83
 * </pre>
 *
 * @author Tim Boudreau
 * @since 2.8.3.1
 */
public final class Illuminant {

    public static final Illuminant IncandescentTungsten = new Illuminant(109.850, 100.000, 35.585, 111.144, 100.000, 35.200, "A", "Incandescent/tungsten");
    public static final Illuminant OldDirectSunlightAtNoon = new Illuminant(99.0927, 100.000, 85.313, 99.178, 100.000, 84.3493, "B", "Old direct sunlight at noon");
    public static final Illuminant OldDaylight = new Illuminant(98.074, 100.000, 118.232, 97.285, 100.000, 116.145, "C", "Old daylight");
    public static final Illuminant IccProfilePCS = new Illuminant(96.422, 100.000, 82.521, 96.720, 100.000, 81.427, "D50", "ICC profile PCS");
    public static final Illuminant MidMorningDaylight = new Illuminant(95.682, 100.000, 92.149, 95.799, 100.000, 90.926, "D55", "Mid-morning daylight");
    public static final Illuminant Daylight_sRGB_AdobeRGB = new Illuminant(95.047, 100.000, 108.883, 94.811, 100.000, 107.304, "D65", "Daylight, sRGB, Adobe-RGB");
    public static final Illuminant NorthSkyDaylight = new Illuminant(94.972, 100.000, 122.638, 94.416, 100.000, 120.641, "D75", "North sky daylight");
    public static final Illuminant EqualEnergy = new Illuminant(100.000, 100.000, 100.000, 100.000, 100.000, 100.000, "E", "Equal energy");
    public static final Illuminant DaylightFluorescent = new Illuminant(92.834, 100.000, 103.665, 94.791, 100.000, 103.191, "F1", "Daylight Fluorescent");
    public static final Illuminant CoolFluorescent = new Illuminant(99.187, 100.000, 67.395, 103.280, 100.000, 69.026, "F2", "Cool fluorescent");
    public static final Illuminant WhiteFluorescent = new Illuminant(103.754, 100.000, 49.861, 108.968, 100.000, 51.965, "F3", "White Fluorescent");
    public static final Illuminant WarmWhiteFluorescent = new Illuminant(109.147, 100.000, 38.813, 114.961, 100.000, 40.963, "F4", "Warm White Fluorescent");
    public static final Illuminant DaylightFluorescent2 = new Illuminant(90.872, 100.000, 98.723, 93.369, 100.000, 98.636, "F5", "Daylight Fluorescent");
    public static final Illuminant LightWhiteFluorescent = new Illuminant(97.309, 100.000, 60.191, 102.148, 100.000, 62.074, "F6", "Lite White Fluorescent");
    public static final Illuminant DaylightFluorescentD65Simulator = new Illuminant(95.044, 100.000, 108.755, 95.792, 100.000, 107.687, "F7", "Daylight fluorescent, D65 simulator");
    public static final Illuminant SylvaniaF40_D50_Simulator = new Illuminant(96.413, 100.000, 82.333, 97.115, 100.000, 81.135, "F8", "Sylvania F40, D50 simulator");
    public static final Illuminant CoolWhiteFluorescent = new Illuminant(100.365, 100.000, 67.868, 102.116, 100.000, 67.826, "F9", "Cool White Fluorescent");
    public static final Illuminant Ultralume50_PhilipsTL85 = new Illuminant(96.174, 100.000, 81.712, 99.001, 100.000, 83.134, "F10", "Ultralume 50, Philips TL85");
    public static final Illuminant Ultralume40_PhilipsTL84 = new Illuminant(100.966, 100.000, 64.370, 103.866, 100.000, 65.627, "F11", "Ultralume 40, Philips TL84");
    public static final Illuminant Ultralume30_PhilipsTL83 = new Illuminant(108.046, 100.000, 39.228, 111.428, 100.000, 40.353, "F12", "Ultralume 30, Philips TL83");

    public static final List<Illuminant> ALL = unmodifiableList(Arrays.asList(
            IncandescentTungsten, OldDirectSunlightAtNoon, OldDaylight,
            IccProfilePCS, MidMorningDaylight, Daylight_sRGB_AdobeRGB,
            NorthSkyDaylight, EqualEnergy, DaylightFluorescent, CoolFluorescent,
            WhiteFluorescent, WarmWhiteFluorescent, DaylightFluorescent2,
            LightWhiteFluorescent, DaylightFluorescentD65Simulator, SylvaniaF40_D50_Simulator,
            CoolWhiteFluorescent, Ultralume50_PhilipsTL85, Ultralume40_PhilipsTL84,
            Ultralume30_PhilipsTL83
    ));

    /**
     * Get the default Illuminant, Adobe SRGB.
     *
     * @return The default illuminant
     */
    public static Illuminant getDefault() {
        return Daylight_sRGB_AdobeRGB;
    }

    public final double x2;
    public final double y2;
    public final double z2;
    public final double x10;
    public final double y10;
    public final double z10;
    public final String name;
    public final String alias;

    private Illuminant(
            double x2, double y2, double z2, // 1931 standard values
            double x10, double y10, double z10, // 1964 standard values
            String alias, String name) {
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.x10 = x10;
        this.y10 = y10;
        this.z10 = z10;
        this.alias = alias;
        this.name = name;
    }

    public static Optional<Illuminant> forName(String s) {
        for (Illuminant il : ALL) {
            if (s.equals(il.name) || s.equals(il.alias)) {
                return Optional.of(il);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return name + "{" + CIE_1931 + "("
                + x(CIE_1931) + ", " + y(CIE_1931) + ", " + z(CIE_1931)
                + ")/" + CIE_1964.name() + "("
                + x(CIE_1964) + ", " + y(CIE_1964) + ", " + z(CIE_1964)
                + ")}";
    }

    public double x(Standard standard) {
        return standard == Standard.CIE_1931 ? x2 : x10;
    }

    public double y(Standard standard) {
        return standard == Standard.CIE_1931 ? y2 : y10;
    }

    public double z(Standard standard) {
        return standard == Standard.CIE_1931 ? z2 : z10;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash
                + (int) (Double.doubleToLongBits(this.x2) ^ (Double.doubleToLongBits(this.x2) >>> 32));
        hash = 41 * hash
                + (int) (Double.doubleToLongBits(this.y2) ^ (Double.doubleToLongBits(this.y2) >>> 32));
        hash = 41 * hash
                + (int) (Double.doubleToLongBits(this.z2) ^ (Double.doubleToLongBits(this.z2) >>> 32));
        hash = 41 * hash
                + (int) (Double.doubleToLongBits(this.x10) ^ (Double.doubleToLongBits(this.x10) >>> 32));
        hash = 41 * hash
                + (int) (Double.doubleToLongBits(this.y10) ^ (Double.doubleToLongBits(this.y10) >>> 32));
        hash = 41 * hash
                + (int) (Double.doubleToLongBits(this.z10) ^ (Double.doubleToLongBits(this.z10) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() != Illuminant.class) {
            return false;
        }
        final Illuminant other = (Illuminant) obj;
        if (Double.doubleToLongBits(this.x2) != Double.doubleToLongBits(other.x2)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y2) != Double.doubleToLongBits(other.y2)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z2) != Double.doubleToLongBits(other.z2)) {
            return false;
        }
        if (Double.doubleToLongBits(this.x10) != Double.doubleToLongBits(other.x10)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y10) != Double.doubleToLongBits(other.y10)) {
            return false;
        }
        return Double.doubleToLongBits(this.z10) == Double.doubleToLongBits(other.z10);
    }
}
