package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * Modifications copyright (C) 2023 SofTeco LLC
 */
public interface ILineRadarDataSet<T extends Entry> extends ILineScatterCandleRadarDataSet<T> {

    /**
     * Returns the color that is used for filling the line surface area.
     *
     * @return
     */
    int getFillColor();

    /**
     * Returns the drawable used for filling the area below the line.
     *
     * @return
     */
    Drawable getFillDrawable();

    /**
     * Returns the alpha value that is used for filling the line surface,
     * default: 85
     *
     * @return
     */
    int getFillAlpha();

    /**
     * Returns the stroke-width of the drawn line
     *
     * @return
     */
    float getLineWidth();

    /**
     * Returns true if filled drawing is enabled, false if not
     *
     * @return
     */
    boolean isDrawFilledEnabled();

    /**
     * Returns true if filled section drawing is enabled, false if not
     *
     * @return
     */
    boolean isDrawFilledSectionEnabled();

    /**
     * Set to true if the DataSet should be drawn filled (surface), and not just
     * as a line, disabling this will give great performance boost. Please note that this method
     * uses the canvas.clipPath(...) method for drawing the filled area.
     * For devices with API level < 18 (Android 4.3), hardware acceleration of the chart should
     * be turned off. Default: false
     *
     * @param enabled
     */
    void setDrawFilled(boolean enabled);

    /**
     * Set the start and end index to fill in the section
     *
     * @param filledStartIndex start index of section filling
     * @param filledEndIndex end index of section filling
     */
    void setDrawFilledSection(int filledStartIndex, int filledEndIndex);

    /**
     * Disables section filling
     */
    void disableFilledSection();

    /**
     * Returns the start and end index of filled section
     *
     * @return
     */
    int[] getIndexesOfDrawFilledSection();
}
