package org.hihn.listenbrainz.model;

public enum TimeRange {

	ALL_TIME("all_time"), MONTH("month"), WEEK("week"), YEAR("year"), QUARTER("quarter"), HALF_YEARLY("half_yearly"),
	THIS_WEEK("this_week"), THIS_MONTH("this_month"), THIS_YEAR("this_year");

	private final String value;

	TimeRange(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
