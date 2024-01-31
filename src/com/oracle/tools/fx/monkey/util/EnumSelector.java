/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.tools.fx.monkey.util;

import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

/**
 * Enum-based Option Selector.
 */
public class EnumSelector<T extends Enum> {
    private final ComboBox<T> field = new ComboBox<>();

    public EnumSelector(Class<T> type, String name, Consumer<T> client) {
        T[] values = type.getEnumConstants();
        FX.name(field, name);
        field.getItems().setAll(values);
        field.getSelectionModel().selectedItemProperty().addListener((p) -> {
            T v = field.getSelectionModel().getSelectedItem();
            client.accept(v);
        });
    }

    public Node node() {
        return field;
    }

    public void select(T v) {
        field.getSelectionModel().select(v);
    }

    public T getValue() {
        return field.getSelectionModel().getSelectedItem();
    }
    
    public T getValue(T defaultValue) {
        T v = getValue();
        return v == null ? defaultValue : v;
    }
}