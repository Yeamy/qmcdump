package com.yeamy.qmcdump;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * launcher / application
 * 
 * @author Yeamy0754
 */
public class MainUI extends Application {
	private ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 2, 0, TimeUnit.SECONDS, //
			new LinkedBlockingDeque<>());
	private File outPath;
	private ArrayList<TaskBean> tasks = new ArrayList<TaskBean>();
	private Label lbTask;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() throws Exception {
		pool.shutdown();
		super.stop();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane pan = new GridPane();
		pan.setPadding(new Insets(5, 10, 5, 10));
		pan.setHgap(5);
		pan.setVgap(10);
		Label lb = new Label("与源文件同目录");
		pan.add(lb, 1, 0);

		lbTask = new Label();
		lbTask.setAlignment(Pos.TOP_LEFT);
		lbTask.prefWidthProperty().bind(pan.widthProperty());
		lbTask.prefHeightProperty().bind(pan.heightProperty());
		pan.add(lbTask, 0, 1, 2, 1);

		Label lbTip = new Label("拖动文件到此窗口");
		lbTip.setAlignment(Pos.CENTER);
		lbTip.prefWidthProperty().bind(pan.widthProperty());
		lbTip.prefHeightProperty().bind(pan.heightProperty());
		pan.add(lbTip, 0, 0, 2, 2);
		lbTip.setOnDragDropped((event) -> {
			Dragboard dragboard = event.getDragboard();
			boolean done = dragboard.hasFiles();
			if (done) {
				lbTip.setText("");
				startWork(dragboard.getFiles());
			}
			event.setDropCompleted(done);
			event.consume();
		});
		// button
		Button btn = new Button("输出目录");
		btn.setMinWidth(80);
		btn.setOnAction((event) -> {
			if (isWorking()) {
				return;
			} else if (outPath == null) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				outPath = directoryChooser.showDialog(primaryStage);
				lb.setText(outPath.getPath());// 选择的文件夹路径
				btn.setText("重置");
			} else {
				outPath = null;
				btn.setText("输出目录");
				lb.setText("与源文件同目录");
			}
		});
		pan.add(btn, 0, 0);

		Scene scene = new Scene(pan, 400, 200);
		scene.setOnDragOver((event) -> {
			Dragboard db = event.getDragboard();
			if (!isWorking() && db.hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		});
		primaryStage.setScene(scene);
		primaryStage.setTitle("QmcDump");
		primaryStage.show();
	}

	private void printText(List<TaskBean> list) {
		StringBuilder sb = new StringBuilder();
		for (TaskBean task : list) {
			task.print(sb);
		}
		Platform.runLater(() -> lbTask.setText(sb.toString()));
	}

	private void startWork(final List<File> list) {
		tasks.clear();
		for (File file : list) {
			tasks.add(new TaskBean(file));
		}
		for (TaskBean task : tasks) {
			pool.execute(() -> {
				if (task.isQmc()) {
					task.setStatus(TaskStatus.DOING);
					printText(tasks);
					File path = task.path(outPath);
					if (QmcDump.dump(task.file, path)) {
						task.setStatus(TaskStatus.SUCCESS);
					} else {
						task.setStatus(TaskStatus.FAIL);
					}
				} else {
					task.setStatus(TaskStatus.FAIL);
				}
				printText(tasks);
			});
		}
	}

	private boolean isWorking() {
		if (tasks.size() == 0) {
			return false;
		}
		for (TaskBean task : tasks) {
			if (!task.finish()) {
				return true;
			}
		}
		return false;
	}
}
