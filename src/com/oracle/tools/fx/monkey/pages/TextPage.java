/*
 * Copyright (c) 2023, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.tools.fx.monkey.pages;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.options.FontOption;
import com.oracle.tools.fx.monkey.options.IntOption;
import com.oracle.tools.fx.monkey.options.TextChoiceOption;
import com.oracle.tools.fx.monkey.sheets.ShapeOptions;
import com.oracle.tools.fx.monkey.util.CheckBoxSelector;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.ShowCharacterRuns;
import com.oracle.tools.fx.monkey.util.Templates;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 * Text Page
 */
public class TextPage extends TestPaneBase {
    private final TextChoiceOption textOption;
    private final CheckBoxSelector showChars;
    private final ScrollPane scroll;
    private final CheckBoxSelector wrap;
    private final Label hitInfo;
    private final Text text;

    public TextPage() {
        FX.name(this, "TextPage");

        text = new Text();
        text.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);

        hitInfo = new Label();

        textOption = new TextChoiceOption("textSelector", true, text.textProperty());
        Utils.fromPairs(Templates.multiLineTextPairs(), (k,v) -> textOption.addChoice(k, v));

        showChars = new CheckBoxSelector("showChars", "show characters", (v) -> updateShowCharacters());

        wrap = new CheckBoxSelector("wrap", "wrap width", (v) -> updateWrap());

        OptionPane op = new OptionPane();
        op.section("Text");

        op.option("Bounds Type:", new EnumOption<>("boundsType", TextBoundsType.class, text.boundsTypeProperty()));

        op.option(new BooleanOption("caretBias", "caret bias (leading)", text.caretBiasProperty()));

        op.option("Caret Position:", new IntOption("caretPosition", -1, Integer.MAX_VALUE, text.caretPositionProperty()));

        op.option("Font:", new FontOption("font", false, text.fontProperty()));

        op.option("Font Smoothing:", new EnumOption<>("fontSmoothing", FontSmoothingType.class, text.fontSmoothingTypeProperty()));

        op.option("Line Spacing:", DoubleOption.lineSpacing("lineSpacing", text.lineSpacingProperty()));

        op.option("Selection Start:", new IntOption("selectionStart", -1, Integer.MAX_VALUE, text.selectionStartProperty()));
        op.option("Selection End:", new IntOption("selectionEnd", -1, Integer.MAX_VALUE, text.selectionEndProperty()));

        op.option("Selection Fill: TODO", null); // TODO

        op.option(new BooleanOption("strikeThrough", "strike through", text.strikethroughProperty()));

        op.option("Tab Size:", IntOption.tabSize("tabSize", text.tabSizeProperty()));

        op.option("Text:", textOption);

        op.option("Text Alignment:", new EnumOption<>("textAlignment", TextAlignment.class, text.textAlignmentProperty()));

        op.option("Text Origin:", new EnumOption<VPos>("textOrigin", VPos.class, text.textOriginProperty()));

        op.option(new BooleanOption("underline", "underline", text.underlineProperty()));

        op.option(wrap.node());

        op.separator();
        op.option(showChars.node());
        op.option("Text.hitTest:", hitInfo);

        ShapeOptions.appendTo(op, text);

        scroll = new ScrollPane();
        scroll.setBorder(Border.EMPTY);
        scroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scroll.setFitToWidth(false);
        scroll.setContent(new Group(text));

        setContent(scroll);
        setOptions(op);

        updateWrap();
        updateShowCharacters();
        textOption.selectFirst();
    }

    private void updateShowCharacters() {
        if (showChars.getValue()) {
            ShowCharacterRuns.createFor(text);
        } else {
            ShowCharacterRuns.remove(text);
        }
    }

    private void updateWrap() {
        if (wrap.getValue()) {
            text.wrappingWidthProperty().bind(scroll.viewportBoundsProperty().map((b) -> b.getWidth()));
        } else {
            text.wrappingWidthProperty().unbind();
            text.setWrappingWidth(0);
        }
    }

    private void handleMouseEvent(MouseEvent ev) {
        Point2D p = new Point2D(ev.getX(), ev.getY());
        HitInfo h = text.hitTest(p);
        hitInfo.setText(String.valueOf(h));
    }
}
