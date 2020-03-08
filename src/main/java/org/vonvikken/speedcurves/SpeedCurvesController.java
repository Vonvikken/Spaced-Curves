/*
 * Class:     org.vonvikken.speedcurves.SpeedCurvesController
 * Author:    Vincenzo Stornanti <von.vikken@gmail.com>
 *
 * Copyright 2020 Vincenzo Stornanti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vonvikken.speedcurves;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedCurvesController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpeedCurvesController.class);
  private static final double MAX_BEND = 250.0;
  private static final double MIN_THICKNESS = 5.0;
  private static final double MAX_THICKNESS = 20.0;
  private static final double MIN_SPACING = 0.0;
  private static final double MAX_SPACING = 400.0;
  private static final double MIN_CURVE_SPACING = 5.0;
  private static final double MAX_CURVE_SPACING = 50.0;
  private static final double MIN_HEIGHT = 0.0;
  private static final double MAX_HEIGHT = 400.0;
  private static final int MIN_CURVES = 1;
  private static final int MAX_CURVES = 20;
  private static final boolean SHOW_GUIDES = false;
  private static final Color INITIAL_COLOR = Color.rgb(210, 159, 36);

  private final Group curveGroup = new Group();
  private final IntegerProperty numberOfCurves = new SimpleIntegerProperty();
  private final DoubleProperty bending = new SimpleDoubleProperty();
  private final DoubleProperty curveThickness = new SimpleDoubleProperty();
  private final DoubleProperty spacing = new SimpleDoubleProperty();
  private final DoubleProperty curveSpacing = new SimpleDoubleProperty();
  private final DoubleProperty height = new SimpleDoubleProperty();
  private final BooleanProperty showLeft = new SimpleBooleanProperty(true);
  private final BooleanProperty showRight = new SimpleBooleanProperty(true);
  private final ObjectProperty<Color> color1 = new SimpleObjectProperty<>();
  private final ObjectProperty<Color> color2 = new SimpleObjectProperty<>();

  @FXML private Pane canvas;

  @FXML private Pane curvePane;

  @FXML private Slider sliderBend;

  @FXML private Slider sliderThickness;

  @FXML private Slider sliderSpacing;

  @FXML private Slider sliderCurveSpacing;

  @FXML private Slider sliderHeight;

  @FXML private ColorPicker colorPicker1;

  @FXML private ColorPicker colorPicker2;

  @FXML private CheckBox left;

  @FXML private CheckBox right;

  @FXML private Label bendingValue;

  @FXML private Label thicknessValue;

  @FXML private Label spacingValue;

  @FXML private Label curveSpacingValue;

  @FXML private Label heightValue;

  @FXML private Spinner<Integer> curvesNumber;

  @FXML
  private void initialize() {

    this.sliderBend.setMax(MAX_BEND);
    this.sliderBend.setMin(-MAX_BEND);
    this.sliderThickness.setMax(MAX_THICKNESS);
    this.sliderThickness.setMin(MIN_THICKNESS);
    this.sliderSpacing.setMax(MAX_SPACING);
    this.sliderSpacing.setMin(MIN_SPACING);
    this.sliderCurveSpacing.setMax(MAX_CURVE_SPACING);
    this.sliderCurveSpacing.setMin(MIN_CURVE_SPACING);
    this.sliderHeight.setMax(MAX_HEIGHT);
    this.sliderHeight.setMin(MIN_HEIGHT);
    this.colorPicker1.setValue(INITIAL_COLOR);
    this.colorPicker2.setValue(Color.TRANSPARENT);
    this.curvesNumber.setValueFactory(new IntegerSpinnerValueFactory(MIN_CURVES, MAX_CURVES));

    // region Bindings
    this.numberOfCurves.bind(this.curvesNumber.valueProperty());
    this.bending.bind(this.sliderBend.valueProperty());
    this.curveThickness.bind(this.sliderThickness.valueProperty());
    this.spacing.bind(this.sliderSpacing.valueProperty());
    this.curveSpacing.bind(this.sliderCurveSpacing.valueProperty());
    this.height.bind(this.sliderHeight.valueProperty());
    this.showLeft.bind(this.left.selectedProperty());
    this.showRight.bind(this.right.selectedProperty());
    this.color1.bind(this.colorPicker1.valueProperty());
    this.color2.bind(this.colorPicker2.valueProperty());

    final DoubleBinding totalWidth =
        this.curveThickness.add(this.curveSpacing).multiply(this.numberOfCurves.add(1));

    this.bendingValue
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> String.format("%d", (int) this.sliderBend.getValue()),
                this.sliderBend.valueProperty()));
    this.thicknessValue
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> String.format("%d", (int) this.sliderThickness.getValue()),
                this.sliderThickness.valueProperty()));

    this.spacingValue
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> String.format("%d", (int) this.sliderSpacing.getValue()),
                this.sliderSpacing.valueProperty()));

    this.curveSpacingValue
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> String.format("%d", (int) this.sliderCurveSpacing.getValue()),
                this.sliderCurveSpacing.valueProperty()));
    this.heightValue
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> String.format("%d", (int) this.sliderHeight.getValue()),
                this.sliderHeight.valueProperty()));
    // endregion

    // region Points
    final ObservablePoint2D pointCenter = new ObservablePoint2D();
    pointCenter.xProperty().bind(this.curvePane.widthProperty().divide(2.0));
    pointCenter.yProperty().bind(this.curvePane.heightProperty().divide(2.0));

    final ObservablePoint2D controlLeft = new ObservablePoint2D();
    controlLeft.xProperty().bind(pointCenter.xProperty().subtract(this.spacing));
    controlLeft.yProperty().bind(pointCenter.yProperty());

    final ObservablePoint2D controlRight = new ObservablePoint2D();
    controlRight.xProperty().bind(pointCenter.xProperty().add(this.spacing));
    controlRight.yProperty().bind(pointCenter.yProperty());

    final ObservablePoint2D startLeft = new ObservablePoint2D();
    startLeft.xProperty().bind(controlLeft.xProperty().add(this.bending));
    startLeft.yProperty().bind(this.height);

    final ObservablePoint2D endLeft = new ObservablePoint2D();
    endLeft.xProperty().bind(controlLeft.xProperty().add(this.bending));
    endLeft.yProperty().bind(this.curvePane.heightProperty().subtract(this.height));

    final ObservablePoint2D startRight = new ObservablePoint2D();
    startRight.xProperty().bind(controlRight.xProperty().subtract(this.bending));
    startRight.yProperty().bind(this.height);

    final ObservablePoint2D endRight = new ObservablePoint2D();
    endRight.xProperty().bind(controlRight.xProperty().subtract(this.bending));
    endRight.yProperty().bind(this.curvePane.heightProperty().subtract(this.height));
    // endregion

    // region Dots
    final Circle dotCenter = new Circle(3, Color.GREEN);
    dotCenter.centerXProperty().bind(pointCenter.xProperty());
    dotCenter.centerYProperty().bind(pointCenter.yProperty());

    final Circle dotControlLeft = new Circle(3, Color.RED);
    dotControlLeft.centerXProperty().bind(controlLeft.xProperty());
    dotControlLeft.centerYProperty().bind(controlLeft.yProperty());

    final Circle dotControlRight = new Circle(3, Color.BLUE);
    dotControlRight.centerXProperty().bind(controlRight.xProperty());
    dotControlRight.centerYProperty().bind(controlRight.yProperty());

    final Circle dotStartLeft = new Circle(1.5, Color.MAGENTA);
    dotStartLeft.centerXProperty().bind(startLeft.xProperty());
    dotStartLeft.centerYProperty().bind(startLeft.yProperty());

    final Circle dotEndLeft = new Circle(1.5, Color.ORANGE);
    dotEndLeft.centerXProperty().bind(controlLeft.xProperty().add(this.bending));
    dotEndLeft.centerYProperty().bind(this.curvePane.heightProperty().subtract(this.height));

    final Circle dotStartRight = new Circle(1.5, Color.CYAN);
    dotStartRight.centerXProperty().bind(startRight.xProperty());
    dotStartRight.centerYProperty().bind(startRight.yProperty());

    final Circle dotEndRight = new Circle(1.5, Color.LIME);
    dotEndRight.centerXProperty().bind(endRight.xProperty());
    dotEndRight.centerYProperty().bind(endRight.yProperty());

    if (SHOW_GUIDES) {
      this.curvePane
          .getChildren()
          .addAll(
              dotCenter,
              dotControlLeft,
              dotControlRight,
              dotStartLeft,
              dotEndLeft,
              dotStartRight,
              dotEndRight);
    }
    // endregion

    // region Crosshairs
    final Line hMedian = new Line();
    hMedian.startXProperty().set(8.0);
    hMedian.endXProperty().bind(this.curvePane.widthProperty());
    hMedian.startYProperty().bind(this.curvePane.heightProperty().divide(2.0));
    hMedian.endYProperty().bind(this.curvePane.heightProperty().divide(2.0));
    hMedian.setStroke(Color.BLACK);
    hMedian.setStrokeWidth(1.0);

    final Line vMedian = this.getPointLine(pointCenter, Color.BLACK);
    final Line leftControlLine = this.getPointLine(controlLeft, Color.GREEN);
    final Line leftStartLine = this.getPointLine(startLeft, Color.CYAN);
    final Line rightControlLine = this.getPointLine(startRight, Color.YELLOW);
    final Line rightStartLine = this.getPointLine(controlRight, Color.MAGENTA);

    this.curvePane.getChildren().addAll(hMedian, vMedian);

    if (SHOW_GUIDES) {
      this.curvePane
          .getChildren()
          .addAll(leftControlLine, leftStartLine, rightControlLine, rightStartLine);
    }
    // endregion

    // region Curves
    this.redrawCurves(
        this.numberOfCurves.get(),
        totalWidth,
        controlLeft,
        controlRight,
        startLeft,
        endLeft,
        startRight,
        endRight);

    this.numberOfCurves.addListener(
        (obs, oldV, newV) ->
            this.redrawCurves(
                newV.intValue(),
                totalWidth,
                controlLeft,
                controlRight,
                startLeft,
                endLeft,
                startRight,
                endRight));

    this.curveGroup.layoutYProperty().bind(this.height.negate());
    // endregion

    // region Canvas
    this.canvas
        .backgroundProperty()
        .bind(
            Bindings.createObjectBinding(
                () ->
                    new Background(
                        new BackgroundFill(
                            new RadialGradient(
                                0.0,
                                0.0,
                                0.5,
                                0.5,
                                0.5,
                                true,
                                CycleMethod.NO_CYCLE,
                                new Stop(0, this.color1.get()),
                                new Stop(50, this.color2.get())),
                            null,
                            null)),
                this.color1,
                this.color2,
                this.height));

    this.canvas.maxWidthProperty().bind(this.curvePane.widthProperty());
    this.canvas
        .maxHeightProperty()
        .bind(this.curvePane.heightProperty().subtract(this.height.multiply(2)));
    this.canvas.setClip(this.curveGroup);
    // endregion
  }

  private void redrawCurves(
      final int num,
      final DoubleBinding totalWidth,
      final ObservablePoint2D controlLeft,
      final ObservablePoint2D controlRight,
      final ObservablePoint2D startLeft,
      final ObservablePoint2D endLeft,
      final ObservablePoint2D startRight,
      final ObservablePoint2D endRight) {

    final List<QuadCurve> curves = new ArrayList<>(2 * (num + 1));

    DoubleStream.iterate(0, v -> v + 1.0 / num)
        .limit(num + 1)
        .forEach(
            v -> {
              LOGGER.debug("V: {}", v);
              final DoubleExpression thickness = this.curveThickness.multiply(1 - v);
              final DoubleBinding rightWidth = totalWidth.multiply(v);
              final DoubleBinding leftWidth = rightWidth.negate();

              final QuadCurve curveLeft =
                  drawCurve(controlLeft, startLeft, endLeft, thickness, leftWidth);
              curveLeft.visibleProperty().bind(this.showLeft);
              curves.add(curveLeft);

              final QuadCurve curveRight =
                  drawCurve(controlRight, startRight, endRight, thickness, rightWidth);
              curveRight.visibleProperty().bind(this.showRight);
              curves.add(curveRight);
            });

    this.curveGroup.getChildren().setAll(curves);
  }

  private static QuadCurve drawCurve(
      final ObservablePoint2D control,
      final ObservablePoint2D start,
      final ObservablePoint2D end,
      final DoubleExpression thickness,
      final DoubleBinding width) {

    final QuadCurve curve = new QuadCurve();

    curve.startXProperty().bind(start.xProperty().add(width));
    curve.startYProperty().bind(start.yProperty());
    curve.endXProperty().bind(end.xProperty().add(width));
    curve.endYProperty().bind(end.yProperty());
    curve.controlXProperty().bind(control.xProperty().add(width));
    curve.controlYProperty().bind(control.yProperty());
    curve.setStroke(Color.BLACK);
    curve.setFill(null);

    final DoubleBinding bound =
        Bindings.createDoubleBinding(
            () -> {
              final double centerX = control.getX() + ((DoubleExpression) width).get();
              final double maxX = curve.getLayoutBounds().getMaxX() - thickness.get() / 2.0;
              final double minX = curve.getLayoutBounds().getMinX() + thickness.get() / 2.0;
              return Double.compare(maxX, centerX) <= 0 ? maxX : minX;
            },
            curve.layoutBoundsProperty(),
            thickness,
            width);

    curve.translateXProperty().bind(curve.controlXProperty().subtract(bound));
    curve.strokeWidthProperty().bind(thickness);
    return curve;
  }

  private Line getVerticalLine(final DoubleExpression xCoord, final Color color) {

    final Line line = new Line();

    line.startXProperty().bind(xCoord);
    line.endXProperty().bind(xCoord);
    line.startYProperty().set(8.0);
    line.endYProperty().bind(this.curvePane.heightProperty());
    line.setStroke(color);
    line.setStrokeWidth(1.0);

    return line;
  }

  private Line getPointLine(final ObservablePoint2D point, final Color color) {
    return this.getVerticalLine(point.xProperty(), color);
  }
}
