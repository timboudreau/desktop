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

import java.awt.Color;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 *
 * @author Tim Boudreau
 */
final class Conversions {

    public static void rgbToXyz(int r, int g, int b, double[] xyz) {
        assert xyz.length >= 3;
        double _r = ((double) r / 255D);
        double _g = (g / 255D);
        double _b = (b / 255D);

        if (_r > 0.04045) {
            _r = Math.pow(((_r + 0.055) / 1.055), 2.4D);

        } else {
            _r /= 12.92;

        }
        if (_g > 0.04045) {
            _g = Math.pow(((_g + 0.055) / 1.055), 2.4D);

        } else {
            _g /= 12.92;

        }
        if (_b > 0.04045) {
            _b = Math.pow(((_b + 0.055) / 1.055), 2.4D);

        } else {
            _b /= 12.92;

        }
        _r = _r * 100D;
        _g = _g * 100D;
        _b = _b * 100D;

        xyz[0] = _r * 0.4124 + _g * 0.3576 + _b * 0.1805;
        xyz[1] = _r * 0.2126 + _g * 0.7152 + _b * 0.0722;
        xyz[2] = _r * 0.0193 + _g * 0.1192 + _b * 0.9505;
    }

    public static void xyzToLab(double x, double y, double z, double[] lab) {
        xyzToLab(x, y, z, lab, Illuminant.getDefault(), Standard.CIE_1964);
    }

    public static void xyzToLab(double x, double y, double z, double[] lab,
            Illuminant illuminant, Standard standard) {
        double workingX = x / illuminant.x(standard);
        double workingY = y / illuminant.y(standard);
        double workingZ = z / illuminant.z(standard);
        if (workingX > 0.008856) {
            workingX = Math.pow(workingX, (1D / 3D));
        } else {
            workingX = (7.787 * workingX) + (16D / 116D);
        }
        if (workingY > 0.008856) {
            workingY = Math.pow(workingY, (1D / 3D));
        } else {
            workingY = (7.787 * workingY) + (16D / 116D);
        }
        if (workingZ > 0.008856) {
            workingZ = Math.pow(workingZ, (1D / 3D));
        } else {
            workingZ = (7.787D * workingZ) + (16D / 116);
        }

        lab[0] = (116D * workingY) - 16D;
        lab[1] = 500D * (workingX - workingY);
        lab[2] = 200D * (workingY - workingZ);
    }

    public static void rgbToLab(int r, int g, int b, double[] lab) {
        rgbToXyz(r, g, b, lab);
        xyzToLab(lab[0], lab[1], lab[2], lab);
    }

    public static void rgbToLab(int[] rgb, double[] lab) {
        rgbToLab(rgb[0], rgb[1], rgb[2], lab);
    }

    public static void xyzToHsb(double[] xyz, float[] hsb) {
        assert xyz.length >= 3;
        xyzToHsb(xyz[0], xyz[1], xyz[2], hsb);
    }

    public static void xyzToHsb(double x, double y, double z, float[] hsb) {
        int[] rgb = new int[3];
        xyzToRgb(x, y, z, rgb);
        Color c = new Color(rgb[0], rgb[1], rgb[2]);
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
    }

    public static void labToRgb(double l, double a, double b, int[] rgb) {
        double[] xyz = new double[3];
        labToXyz(l, a, b, xyz);
        xyzToRgb(xyz[0], xyz[1], xyz[2], rgb);
    }

    public static void labToRgb(double l, double a, double b, int[] rgb, Illuminant ill, Standard st) {
        double[] xyz = new double[3];
        labToXyz(l, a, b, xyz, ill, st);
        xyzToRgb(xyz[0], xyz[1], xyz[2], rgb);
    }

    public static void labToHsb(double[] lab, float[] hsb) {
        assert lab.length >= 3;
        labToHsb(lab[0], lab[1], lab[2], hsb);
    }

    public static void labToHsb(double l, double a, double b, float[] hsb) {
        int[] rgb = new int[3];
        labToRgb(l, a, b, rgb);
        Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
    }

    public static void hsbToLab(float[] hsb, double[] lab) {
        hsbToLab(hsb[0], hsb[1], hsb[2], lab);
    }

    public static void hsbToLab(double h, double s, double b, double[] lab) {
        hsbToXyz(h, s, b, lab);
        xyzToLab(lab[0], lab[1], lab[2], lab);
    }

    public static void hsbToXyz(float[] hsb, double[] xyz) {
        hsbToXyz(hsb[0], hsb[1], hsb[2], xyz);
    }

    public static void hsbToXyz(double h, double s, double b, double[] xyz) {
        Color c = new Color(Color.HSBtoRGB((float) h, (float) s, (float) b));
        rgbToXyz(c.getRed(), c.getGreen(), c.getBlue(), xyz);
    }

    public static void xyzToRgb(double x, double y, double z, int[] comps) {
        assert comps.length >= 3;
        double var_X = x / 100;
        double var_Y = y / 100;
        double var_Z = z / 100;

        double var_R = var_X * 3.2406 + var_Y * -1.5372 + var_Z * -0.4986;
        double var_G = var_X * -0.9689 + var_Y * 1.8758 + var_Z * 0.0415;
        double var_B = var_X * 0.0557 + var_Y * -0.2040 + var_Z * 1.0570;

        if (var_R > 0.0031308) {
            var_R = 1.055 * (Math.pow(var_R, (1D / 2.4D))) - 0.055;
        } else {
            var_R = 12.92 * var_R;
        }
        if (var_G > 0.0031308) {
            var_G = 1.055 * (Math.pow(var_G, (1D / 2.4D))) - 0.055;
        } else {
            var_G = 12.92 * var_G;
        }
        if (var_B > 0.0031308) {
            var_B = 1.055 * (Math.pow(var_B, (1D / 2.4D))) - 0.055;
        } else {
            var_B = 12.92 * var_B;
        }

        comps[0] = (int) Math.max(0, Math.min(255, var_R * 255));
        comps[1] = (int) Math.max(0, Math.min(255, var_G * 255));
        comps[2] = (int) Math.max(0, Math.min(255, var_B * 255));
    }

    public static void labToXyz(double[] lab, double[] xyz) {
        labToXyz(lab[0], lab[1], lab[2], xyz);
    }

    public static void labToXyz(double l, double a, double b, double[] xyz) {
        labToXyz(l, a, b, xyz, Illuminant.getDefault(), Standard.CIE_1964);
    }

    public static void labToXyz(double l, double a, double b, double[] xyz, Illuminant illuminant, Standard standard) {
        double var_Y = (l + 16D) / 116D;
        double var_X = a / 500D + var_Y;
        double var_Z = var_Y - b / 200D;

        if (Math.pow(var_Y, 3D) > 0.008856) {
            var_Y = Math.pow(var_Y, 3D);
        } else {
            var_Y = (var_Y - 16D / 116D) / 7.787;
        }
        if (Math.pow(var_X, 3D) > 0.008856) {
            var_X = Math.pow(var_X, 3D);

        } else {
            var_X = (var_X - 16D / 116D) / 7.787;
        }
        if (Math.pow(var_Z, 3) > 0.008856) {
            var_Z = Math.pow(var_Z, 3D);
        } else {
            var_Z = (var_Z - 16D / 116D) / 7.787;
        }
        xyz[0] = var_X * illuminant.x(standard);
        xyz[1] = var_Y * illuminant.y(standard);
        xyz[2] = var_Z * illuminant.z(standard);
    }

    public static void xyzToLab(double[] xyz, double[] lab) {
        assert xyz.length >= 3 && lab.length >= 3;
        xyzToLab(xyz[0], xyz[1], xyz[2], lab);
    }

    public static void rgbToXyz(int[] rgb, double[] xyz) {
        rgbToXyz(rgb[0], rgb[1], rgb[2], xyz);
    }

    public static Color labToColor(double l, double a, double b) {
        int[] rgb = new int[3];
        labToRgb(l, a, b, rgb);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    public static Color xyzToColor(double x, double y, double z) {
        int[] rgb = new int[3];
        xyzToRgb(x, y, z, rgb);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    public static void rgbToHsb(int r, int g, int b, double[] hsb) {
        double hue, saturation, brightness;
        if (hsb == null) {
            hsb = new double[3];
        }
        double cmax = max(r, max(g, b));
        double cmin = min(r, min(g, b));

        brightness = (cmax) / 255.0D;
        if (cmax != 0) {
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        } else {
            saturation = 0;
        }
        if (saturation == 0) {
            hue = 0;
        } else {
            double redComponent = ((double) (cmax - r)) / ((double) (cmax - cmin));
            double greenComponent = ((double) (cmax - g)) / ((double) (cmax - cmin));
            double blueComponent = ((double) (cmax - b)) / ((double) (cmax - cmin));
            if (r == cmax) {
                hue = blueComponent - greenComponent;
            } else if (g == cmax) {
                hue = 2.0D + redComponent - blueComponent;
            } else {
                hue = 4.0D + greenComponent - redComponent;
            }
            hue = hue / 6.0D;
            if (hue < 0) {
                hue = hue + 1.0D;
            }
        }
        hsb[0] = hue;
        hsb[1] = saturation;
        hsb[2] = brightness;
    }
    
    public static int hsbToRgb(double h, double s, double b) {
        return Color.HSBtoRGB((float) h, (float) s, (float) b);
    }
    
    private Conversions() {
        throw new AssertionError();
    }
}
