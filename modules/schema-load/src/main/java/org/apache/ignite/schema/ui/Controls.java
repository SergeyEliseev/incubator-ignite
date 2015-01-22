/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.schema.ui;

import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.*;

/**
 * Utility class to create controls.
 */
public class Controls {
    /** */
    public static final Insets DFLT_PADDING = new Insets(10, 10, 10, 10);

    /**
     * Create scene with predefined style.
     *
     * @param root The root node of the scene graph.
     * @return New {@code Scene} instance.
     */
    public static Scene scene(Parent root) {
        Scene scene = new Scene(root);

        scene.getStylesheets().add("media/style.css");

        return scene;
    }

    /**
     * Create grid pane with default padding.
     *
     * @param top Top padding
     * @param right Right padding.
     * @param bottom Bottom padding.
     * @param left Left padding.
     * @return New {@code GridPaneEx} instance.
     */
    public static GridPaneEx paneEx(double top, double right, double bottom, double left) {
        GridPaneEx paneEx = new GridPaneEx();

        paneEx.setPadding(new Insets(top, right, bottom, left));

        return paneEx;
    }

    /**
     * Create new {@code HBox} with default padding.
     *
     * @param spacing Amount of horizontal space between each child.
     * @param dfltPadding If {@code true} than set default padding for pane.
     * @return New {@code HBox} instance.
     */
    public static HBox hBox(int spacing, boolean dfltPadding) {
        HBox hb = new HBox(spacing);

        if (dfltPadding)
            hb.setPadding(DFLT_PADDING);

        return hb;
    }

    /**
     * Create new {@code HBox} with default padding and add controls.
     *
     * @param spacing Amount of horizontal space between each child.
     * @param dfltPadding If {@code true} than set default padding for pane.
     * @param controls Controls to add.
     * @return New {@code HBox} instance.
     */
    public static HBox hBox(int spacing, boolean dfltPadding, Node... controls) {
        HBox hb = hBox(spacing, dfltPadding);

        hb.getChildren().addAll(controls);

        return hb;
    }

    /**
     * Create new {@code VBox} with default padding.
     *
     * @param spacing Amount of horizontal space between each child.
     * @return New {@code VBox} instance.
     */
    public static VBox vBox(int spacing) {
        VBox vb = new VBox(spacing);

        vb.setPadding(DFLT_PADDING);

        return vb;
    }

    /**
     * Create new {@code VBox} with default padding and add controls.
     *
     * @param spacing Amount of horizontal space between each child.
     * @param controls Controls to add.
     * @return New {@code VBox} instance.
     */
    public static VBox vBox(int spacing, Node... controls) {
        VBox vb = vBox(spacing);

        vb.getChildren().addAll(controls);

        return vb;
    }

    /**
     * Create stack pane.
     *
     * @param controls Controls to add.
     * @return New {@code StackPane} instance.
     */
    public static StackPane stackPane(Node... controls) {
        StackPane sp = new StackPane();

        sp.getChildren().addAll(controls);

        return sp;
    }

    /**
     * Create border pane.
     *
     * @param top Optional top control.
     * @param center Optional center control.
     * @param bottom Optional bottom control.
     * @param left Optional left control.
     * @param right Optional right control.
     * @return New {@code BorderPane} instance.
     */
    public static BorderPane borderPane(Node top, Node center, Node bottom, Node left, Node right) {
        BorderPane bp = new BorderPane();

        bp.setTop(top);
        bp.setCenter(center);
        bp.setBottom(bottom);
        bp.setLeft(left);
        bp.setRight(right);

        return bp;
    }

    /**
     * Sets control tooltip if needed.
     *
     * @param ctrl Target control.
     * @param tip Tooltip text.
     * @return Control itself for method chaining.
     */
    public static <T extends Control> T tooltip(T ctrl, String tip) {
        if (!tip.isEmpty())
            ctrl.setTooltip(new Tooltip(tip));

        return ctrl;
    }

    /**
     * Create button with text only.
     *
     * @param text Button text.
     * @param tip Tooltip text.
     * @param onAct Button action.
     * @return New {@code Button} instance.
     */
    public static Button button(String text, String tip, EventHandler<ActionEvent> onAct) {
        Button btn = new Button(text);

        btn.setOnAction(onAct);

        tooltip(btn, tip);

        return btn;
    }

    /**
     * Create button with icon only.
     *
     * @param icon Button icon.
     * @param tip Tooltip text.
     * @param onAct Button action.
     * @return New {@code Button} instance.
     */
    public static Button button(ImageView icon, String tip, EventHandler<ActionEvent> onAct) {
        Button btn = new Button();

        btn.setGraphic(icon);
        btn.setOnAction(onAct);

        tooltip(btn, tip);

        return btn;
    }

    /**
     * Create pane with buttons.
     *
     * @param alignment Alignment of buttons.
     * @param dfltPadding If {@code true} than set default padding for pane.
     * @param btns Buttons that will be added to pane.
     * @return New {@code HBox} instance with buttons.
     */
    public static Pane buttonsPane(Pos alignment, boolean dfltPadding, Button... btns) {
        HBox hb = hBox(10, dfltPadding, btns);

        hb.setAlignment(alignment);

        return hb;
    }

    /**
     * Create checkbox.
     *
     * @param text Checkbox text.
     * @param tip Tooltip tex.
     * @param sel Checkbox selected state.
     * @return New {@code Checkbox} instance.
     */
    public static CheckBox checkBox(String text, String tip, boolean sel) {
        CheckBox ch = new CheckBox(text);

        ch.setSelected(sel);

        tooltip(ch, tip);

        return ch;
    }

    /**
     * Create text field.
     *
     * @param tip Tooltip text.
     * @return New {@code TextField} instance.
     */
    public static TextField textField(String tip) {
        TextField tf = new TextField();

        tooltip(tf, tip);

        return tf;
    }

    /**
     * Create static text.
     *
     * @param text Text to show.
     * @param wrap Text wrapping width.
     * @return New {@code Text} instance.
     */
    public static Text text(String text, int wrap) {
        Text t = new Text(text);

        t.setFont(new Font(14));

        if (wrap > 0)
            t.setWrappingWidth(wrap);

        return t;
    }

    /**
     * Create password field.
     *
     * @param tip Tooltip text.
     * @return New {@code PasswordField} instance.
     */
    public static PasswordField passwordField(String tip) {
        PasswordField pf = new PasswordField();

        tooltip(pf, tip);

        return pf;
    }

    /**
     * Create split pane for provided nodes.
     *
     * @param node1 First node.
     * @param node2 Second node.
     * @param pos Initial divider position.
     * @return New {@code SplitPane} instance.
     */
    public static SplitPane splitPane(Node node1, Node node2, double pos) {
        SplitPane sp = new SplitPane();

        sp.setOrientation(Orientation.VERTICAL);
        sp.getItems().addAll(node1, node2);
        sp.setDividerPosition(0, pos);

        return sp;
    }

    /**
     * Create table column.
     *
     * @param colName Column name to display.
     * @param propName Property name column is bound to.
     * @param tip Column tooltip text.
     * @param minWidth The minimum width column is permitted to be resized to.
     * @param maxWidth The maximum width column is permitted to be resized to.
     * @param editable {@code true} if column is editable.
     * @return New {@code TableColumn} instance.
     */
    private static <S, T> TableColumn<S, T> tableColumn(String colName, String propName, String tip,
        int minWidth, int maxWidth, boolean editable) {
        TableColumn<S, T> col = new TableColumn<>();

        col.setGraphic(tooltip(new Label(colName), tip));

        col.setSortable(false);

        if (minWidth > 0)
            col.setMinWidth(minWidth);

        if (maxWidth > 0)
            col.setMaxWidth(maxWidth);

        col.setCellValueFactory(new PropertyValueFactory<S, T>(propName));

        col.setEditable(editable);

        return col;
    }

    /**
     * Create table column.
     *
     * @param colName Column name to display.
     * @param propName Property name column is bound to.
     * @param tip Column tooltip text.
     * @return New {@code TableColumn} instance.
     */
    public static <S, T> TableColumn<S, T> tableColumn(String colName, String propName, String tip) {
        return tableColumn(colName, propName, tip, 100, 0, false);
    }

    /**
     * Create table column.
     *
     * @param colName Column name to display.
     * @param propName Property name column is bound to.
     * @param tip Column tooltip text.
     * @param cellFactory Custom cell factory.
     * @return New {@code TableColumn} instance.
     */
    public static <S, T> TableColumn<S, T> customColumn(String colName, String propName, String tip,
        Callback<TableColumn<S, T>, TableCell<S, T>> cellFactory) {
        TableColumn<S, T> col = tableColumn(colName, propName, tip, 100, 0, true);

        col.setCellFactory(cellFactory);

        return col;
    }

    public static <S> TableColumn<S, Boolean> booleanColumn(String colName, String propName, String tip) {
        TableColumn<S, Boolean> col = tableColumn(colName, propName, tip, 70, 70, true);

        col.setCellFactory(CheckBoxTableCellEx.<S>forTableColumn());

        return col;

    }

    public static <S> TableColumn<S, String> textColumn(String colName, String propName, String tip) {
        TableColumn<S, String> col = tableColumn(colName, propName, tip, 100, 0, true);

        col.setCellFactory(TextFieldTableCellEx.<S>forTableColumn());

        return col;
    }

    /**
     * Create table column.
     *
     * @param colName Column name to display.
     * @param propName Property name column is bound to.
     * @param tip Column tooltip text.
     * @param editable {@code true} if column is editable.
     * @return New {@code TableColumn} instance.
     */
    public static <S, T> TableColumn<S, T> tableColumn(String colName, String propName, String tip, boolean editable) {
        return tableColumn(colName, propName, tip, 100, 0, editable);
    }

    /**
     * Create table view.
     *
     * @param placeholder Text to show if table model is empty.
     * @param cols Columns to add.
     * @return New {@code TableView} instance.
     */
    public static <S> TableView<S> tableView(String placeholder, TableColumn<S, ?>... cols) {
        TableView<S> tbl = new TableView<>();

        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbl.setEditable(true);
        tbl.setMinHeight(50);
        tbl.setPlaceholder(text(placeholder, 0));

        tbl.getColumns().addAll(cols);

        return tbl;
    }

    /**
     * Create progress indicator.
     *
     * @param sz Indicator diameter.
     * @return New {@code ProgressIndicator} instance.
     */
    public static ProgressIndicator progressIndicator(int sz) {
        ProgressIndicator pi = new ProgressIndicator();

        pi.setMaxWidth(sz);
        pi.setMaxHeight(sz);

        return pi;
    }

    /**
     * Create image view.
     *
     * @param imgFileName Image filename.
     * @return New {@code ImageView} instance.
     */
    public static ImageView imageView(String imgFileName, int sz) {
        return new ImageView(image(imgFileName, sz));
    }

    /**
     * Gets image by its filename.
     *
     * @param imgFileName Image filename.
     * @return Loaded image.
     */
    public static Image image(String imgFileName, int sz) {
        return new Image(Controls.class.getClassLoader()
            .getResourceAsStream(String.format("media/%1$s_%2$dx%2$d.png", imgFileName, sz)));
    }

    /**
     * Customized checkbox.
     */
    private static class CheckBoxTableCellEx<S> extends CheckBoxTableCell<S, Boolean> {
        /** Creates a ComboBox cell factory for use in TableColumn controls. */
        public static <S> Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> forTableColumn() {
            return new Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>>() {
                public TableCell<S, Boolean> call(TableColumn<S, Boolean> col) {
                    return new CheckBoxTableCellEx<>();
                }
            };
        }

        /**
         * Default constructor.
         */
        private CheckBoxTableCellEx() {
            super();

            setAlignment(Pos.CENTER);
        }
    }

    private static class TextFieldTableCellEx<S> extends TableCell<S, String> {
        /** Text field. */
        private final TextField textField;

        public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
            return new Callback<TableColumn<S, String>, TableCell<S, String>>() {
                @Override public TableCell<S, String> call(TableColumn<S, String> col) {
                    return new TextFieldTableCellEx<>();
                }
            };
        }

        private TextFieldTableCellEx() {
            textField = new TextField();

            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
                /** {@inheritDoc} */
                @Override public void handle(KeyEvent evt) {
                    if (KeyCode.ENTER == evt.getCode())
                        commitEdit(textField.getText());
                    else if (KeyCode.ESCAPE == evt.getCode())
                        cancelEdit();
                }
            });

            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                /** {@inheritDoc} */
                @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal,
                    Boolean newVal) {
                    if (!newVal)
                        commitEdit(textField.getText());
                }
            });

            getStyleClass().add("text-field-table-cell");
        }

        /** {@inheritDoc} */
        @Override public void startEdit() {
            super.startEdit();

            setText(null);
            setGraphic(textField);
        }

        /** {@inheritDoc} */
        @Override public void cancelEdit() {
            super.cancelEdit();

            setText(getItem());

            setGraphic(null);
        }

        /** {@inheritDoc} */
        @Override public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            setGraphic(null);

            if (!empty) {
                setText(item);

                textField.setText(item);
            }
        }
    }
}