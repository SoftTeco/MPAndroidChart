package com.github.mikephil.charting.interfaces.datasets;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by philipp on 21/10/15.
 * Modifications copyright (C) 2023 SoftTeco LLC
 */
public interface IBarLineScatterCandleBubbleDataSet<T extends Entry> extends IDataSet<T> {

    /**
     * Returns the color that is used for drawing the highlight indicators.
     *
     * @return
     */
    int getHighLightColor();

    /**
     * Returns the color that is used for drawing the active highlight indicator.
     *
     * @return
     */
    int getActiveHighLightColorForSection();
}
