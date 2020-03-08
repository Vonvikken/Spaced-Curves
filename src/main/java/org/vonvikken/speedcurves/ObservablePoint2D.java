/*
 * Class:     org.vonvikken.speedcurves.ObservablePoint2D
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

class ObservablePoint2D extends SimpleObjectProperty<Point2D> {

  private final DoubleProperty xCoord = new SimpleDoubleProperty();
  private final DoubleProperty yCoord = new SimpleDoubleProperty();

  ObservablePoint2D() {
    this(0.0, 0.0);
  }

  ObservablePoint2D(final double x, final double y) {
    this.set(new Point2D(x, y));
    this.xCoord.set(x);
    this.yCoord.set(y);

    this.xCoord.addListener(
        (obs, oldV, newV) -> this.set(new Point2D(newV.doubleValue(), this.get().getY())));
    this.yCoord.addListener(
        (obs, oldV, newV) -> this.set(new Point2D(this.get().getX(), newV.doubleValue())));
  }

  void setX(final double x) {
    this.xCoord.set(x);
  }

  void setY(final double y) {
    this.yCoord.set(y);
  }

  double getX() {
    return this.get().getX();
  }

  double getY() {
    return this.get().getY();
  }

  DoubleProperty xProperty() {
    return this.xCoord;
  }

  DoubleProperty yProperty() {
    return this.yCoord;
  }
}
