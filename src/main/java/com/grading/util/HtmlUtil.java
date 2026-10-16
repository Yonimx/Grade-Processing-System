package com.grading.util;

import java.util.Locale;

/**
 * Small helpers used by the JSP pages.
 */
public class HtmlUtil {

	/** Escapes text so user input (e.g. student names) is shown as text, not HTML. */
	public static String esc(String text) {
		if (text == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (char c : text.toCharArray()) {
			switch (c) {
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '&': sb.append("&amp;"); break;
				case '"': sb.append("&quot;"); break;
				case '\'': sb.append("&#39;"); break;
				default: sb.append(c);
			}
		}
		return sb.toString();
	}

	/** Formats a number with two decimal places. */
	public static String fmt(double value) {
		return String.format(Locale.US, "%.2f", value);
	}

	/** CSS class for a letter grade badge, e.g. "B+" -> "grade-b". */
	public static String gradeClass(String letter) {
		return "grade-" + Character.toLowerCase(letter.charAt(0));
	}

	/** CSS class for a status badge, e.g. "Academic Warning" -> "status-academic-warning". */
	public static String statusClass(String status) {
		return "status-" + status.toLowerCase().replace(' ', '-');
	}
}
