/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.util.chart;

import ec.util.chart.TimeSeriesChart.RendererType;
import ec.util.chart.TimeSeriesChart.Element;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Defines a command pattern on a time series chart.
 *
 * @author Philippe Charles
 */
public abstract class TimeSeriesChartCommand {

    /**
     * Executes this command on the specified time series chart.
     *
     * @param chart the input chart
     */
    abstract public void execute(@Nonnull TimeSeriesChart chart);

    /**
     * Checks if this command should be enabled with the specified time series
     * chart.
     *
     * @param chart the input chart
     * @return true if enabled; false otherwise
     */
    public boolean isEnabled(@Nonnull TimeSeriesChart chart) {
        return true;
    }

    /**
     * Checks if this command should be marked as selected with the specified
     * time series chart.
     *
     * @param chart the input chart
     * @return true if selected; false otherwise
     */
    public boolean isSelected(@Nonnull TimeSeriesChart chart) {
        return false;
    }

    /**
     * Creates a time series chart command that resets all the properties.
     *
     * @return a non-null command
     */
    @Nonnull
    public static TimeSeriesChartCommand reset() {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setDataset(null);
                chart.setColorSchemeSupport(null);
                chart.setTitle(null);
                chart.setNoDataMessage(null);
                for (Element o : Element.values()) {
                    chart.setElementVisible(o, true);
                }
                chart.setPlotWeights(null);
                chart.setPlotDispatcher(null);
                chart.setLineThickness(1f);
                chart.setPeriodFormat(null);
                chart.setValueFormat(null);
                chart.setSeriesRenderer(null);
                chart.setSeriesFormatter(null);
                chart.setObsFormatter(null);
                chart.setDashPredicate(null);
                chart.setLegendVisibilityPredicate(null);
            }
        };
    }

    /**
     * Creates a time series chart command that clears all the data.
     *
     * @return a non-null command
     */
    @Nonnull
    public static TimeSeriesChartCommand clearDataset() {
        return CLEAR;
    }

    /**
     * Creates a time series chart command that toggles the visibility of an
     * element.
     *
     * @param element the element to modify
     * @return a non-null command
     */
    @Nonnull
    public static TimeSeriesChartCommand toggleElementVisibility(@Nonnull Element element) {
        switch (element) {
            case AXIS:
                return EV.AXIS;
            case LEGEND:
                return EV.LEGEND;
            case TITLE:
                return EV.TITLE;
        }
        throw new RuntimeException("Unreachable code");
    }

    /**
     * Creates a time series chart command that applies a specific line
     * thickness.
     *
     * @param thickness the line thickness to apply
     * @return a non-null command
     */
    @Nonnull
    public static TimeSeriesChartCommand applyLineThickness(final float thickness) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setLineThickness(thickness);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return chart.getLineThickness() == thickness;
            }
        };
    }

    /**
     * Creates a time series chart command that sets the predicate that
     * determines if an observation should be dashed.
     *
     * @param predicate the specified predicate
     * @return a non-null command
     */
    @Nonnull
    public static TimeSeriesChartCommand applyDash(@Nullable final ObsPredicate predicate) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setDashPredicate(predicate);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return chart.getDashPredicate().equals(predicate);
            }
        };
    }

    /**
     * Creates a time series chart command that sets the predicates that
     * determines if a series is shown in the legend.
     *
     * @param predicate the specified predicate
     * @return a non-null command
     */
    @Nonnull
    public static TimeSeriesChartCommand applyLegendVisibility(@Nullable final SeriesPredicate predicate) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setLegendVisibilityPredicate(predicate);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return chart.getLegendVisibilityPredicate().equals(predicate);
            }
        };
    }

    @Nonnull
    public static TimeSeriesChartCommand applyRenderer(@Nullable final SeriesFunction<RendererType> renderer) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setSeriesRenderer(renderer);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return chart.getSeriesRenderer().equals(renderer);
            }
        };
    }

    @Nonnull
    public static TimeSeriesChartCommand applyRenderer(@Nonnull RendererType... typeIndex) {
        return applyRenderer(SeriesFunction.array(typeIndex));
    }

    @Nonnull
    public static TimeSeriesChartCommand applyPlotDispatcher(@Nullable final SeriesFunction<Integer> plotDispatcher) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setPlotDispatcher(plotDispatcher);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return chart.getPlotDispatcher().equals(plotDispatcher);
            }
        };
    }

    @Nonnull
    public static TimeSeriesChartCommand applyPlotDispatcher(@Nonnull Integer... plotIndex) {
        return applyPlotDispatcher(SeriesFunction.array(plotIndex));
    }

    @Nonnull
    public static TimeSeriesChartCommand applySeriesFormatter(@Nullable final SeriesFunction<String> formatter) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setSeriesFormatter(formatter);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return chart.getSeriesFormatter().equals(formatter);
            }
        };
    }

    @Nonnull
    public static TimeSeriesChartCommand applySeriesFormatter(@Nonnull String... values) {
        return applySeriesFormatter(SeriesFunction.array(values));
    }

    @Nonnull
    public static TimeSeriesChartCommand applyPeriod(@Nullable final DateFormat periodFormat) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setPeriodFormat(periodFormat);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return chart.getPeriodFormat().equals(periodFormat);
            }
        };
    }

    @Nonnull
    public static TimeSeriesChartCommand applyPeriod(String format) {
        return applyPeriod(new SimpleDateFormat(format));
    }

    @Nonnull
    public static TimeSeriesChartCommand applyWeights(@Nonnull final int... weights) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setPlotWeights(weights);
            }

            @Override
            public boolean isSelected(TimeSeriesChart chart) {
                return Arrays.equals(weights, chart.getPlotWeights());
            }
        };
    }

    @Nonnull
    public static TimeSeriesChartCommand applyTitle(@Nullable final String title) {
        return new TimeSeriesChartCommand() {
            @Override
            public void execute(TimeSeriesChart chart) {
                chart.setTitle(title);
            }
        };
    }

    @Nonnull
    public static TimeSeriesChartCommand copyImage() {
        return COPY_IMAGE;
    }

    @Nonnull
    public static TimeSeriesChartCommand saveImage() {
        return SAVE_IMAGE;
    }

    @Nonnull
    public static TimeSeriesChartCommand printImage() {
        return PRINT_IMAGE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final TimeSeriesChartCommand CLEAR = new TimeSeriesChartCommand() {
        @Override
        public void execute(TimeSeriesChart chart) {
            chart.setDataset(null);
        }
    };

    private static class EV extends TimeSeriesChartCommand {

        private static final EV TITLE = new EV(Element.TITLE), LEGEND = new EV(Element.LEGEND), AXIS = new EV(Element.AXIS);
        final TimeSeriesChart.Element element;

        public EV(Element element) {
            this.element = element;
        }

        @Override
        public void execute(TimeSeriesChart chart) {
            chart.setElementVisible(element, !chart.isElementVisible(element));
        }

        @Override
        public boolean isSelected(TimeSeriesChart chart) {
            return chart.isElementVisible(element);
        }
    }
    private static final TimeSeriesChartCommand COPY_IMAGE = new TimeSeriesChartCommand() {
        @Override
        public void execute(TimeSeriesChart chart) {
            try {
                chart.copyImage();
            } catch (IOException ex) {
                Logger.getLogger(TimeSeriesChartCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    private static final TimeSeriesChartCommand SAVE_IMAGE = new TimeSeriesChartCommand() {
        @Override
        public void execute(TimeSeriesChart chart) {
            try {
                chart.saveImage();
            } catch (IOException ex) {
                Logger.getLogger(TimeSeriesChartCommand.class.getName()).warning(ex.getMessage());
            }
        }
    };
    private static final TimeSeriesChartCommand PRINT_IMAGE = new TimeSeriesChartCommand() {
        @Override
        public void execute(TimeSeriesChart chart) {
            try {
                chart.printImage();
            } catch (IOException ex) {
                Logger.getLogger(TimeSeriesChartCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    //</editor-fold>
}
