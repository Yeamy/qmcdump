package com.yeamy.qmcdump;

import java.io.File;

/**
 * task info for application
 * 
 * @author Yeamy0754
 */
public class TaskBean {

	public File file;

	public TaskStatus status = TaskStatus.WAIT;

	public TaskBean(File file) {
		this.file = file;
	}

	public boolean isQmc() {
		String name = file.getName();
		int i = name.lastIndexOf('.');
		return name.indexOf(".qmc", i) == i;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public File path(File outPath) {
		return outPath != null ? outPath : file.getParentFile();
	}

	public void print(StringBuilder sb) {
		sb.append(file.getName());
		switch (status) {
		case DOING:
			sb.append("(正在处理)\n");
			break;
		case SUCCESS:
			sb.append(" √\n");
			break;
		case FAIL:
			sb.append(" ×\n");
			break;
		case WAIT:
		default:
			sb.append("\n");
			break;
		}

	}

	public boolean finish() {
		return status == TaskStatus.SUCCESS || status == TaskStatus.FAIL;
	}

}
