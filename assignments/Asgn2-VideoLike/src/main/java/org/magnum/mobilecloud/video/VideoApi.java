package org.magnum.mobilecloud.video;

public class VideoApi {
	public static final String VIDEO_PATH = "/video";
	
	public static final String LIKE_PATH = VideoApi.VIDEO_PATH + "/{id}/" + VideoApi.LIKE_PARAMETER;
	
	public static final String UNLIKE_PATH = VideoApi.VIDEO_PATH + "/{id}/" + VideoApi.UNLIKE_PARAMETER;

	public static final String LIKED_BY_PATH = VIDEO_PATH + "/{id}/likedby";

	public static final String LIKE_PARAMETER = "like";
	
	public static final String UNLIKE_PARAMETER = "unlike";

	public static final String TITLE_PARAMETER = "title";

	public static final String DURATION_PARAMETER = "duration";

	public static final String FIND_BY_NAME_PATH = VIDEO_PATH + "/search/findByName";

	public static final String FIND_BY_DURATION_LESS_PATH = VIDEO_PATH + "/search/findByDurationLessThan";

	
}
