package org.hihn.listenbrainz.model;

public enum ArtistType {

	TOP("top"), SIMILAR("similar"), RAW("raw");

	private final String value;

	ArtistType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
