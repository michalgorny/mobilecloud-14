/*
 * 
 * Copyright 2014 Jules White
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
 * 
 */
package org.magnum.dataup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoController {

	private static final String VIDEO_PATH = "/video";

	public static final String DATA_PARAMETER = "data";

	public static final String ID_PARAMETER = "id";

	public static final String VIDEO_DATA_PATH = VIDEO_PATH + "/{id}/data";

	private static final AtomicLong currentId = new AtomicLong(0L);

	private Map<Long, Video> videos = new HashMap<Long, Video>();

	@ResponseBody
	@RequestMapping(value = VIDEO_PATH, method = RequestMethod.GET)
	public Collection<Video> getVideoList() {
		return videos.values();
	}

	@ResponseBody
	@RequestMapping(value = VIDEO_PATH, method = RequestMethod.POST)
	public Video addVideo(@RequestBody Video video) {
		save(video);
		video.setDataUrl(getDataUrl(video.getId()));
		return video;
	}

	@ResponseBody 
	@RequestMapping(value = VIDEO_DATA_PATH, method = RequestMethod.POST)
	public VideoStatus setVideoData(
			@PathVariable(ID_PARAMETER) long id,
			@RequestParam(DATA_PARAMETER) MultipartFile data) {

		if (!videos.containsKey(id)) {
			throw new ResourceNotFoundException();
		}

		try {
			VideoFileManager videoFileManager = VideoFileManager.get();

			Video video = videos.get(id);
			InputStream videoData = data.getInputStream();

			videoFileManager.saveVideoData(video, videoData);

			return new VideoStatus(VideoState.READY);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(); // TODO Return appropriate HTTP error status
		}

	}

	@ResponseBody
	@RequestMapping(value = VIDEO_DATA_PATH, method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public byte[] getVideoData(@PathVariable(value = ID_PARAMETER) long id) {
		if (!videos.containsKey(id)) {
			throw new ResourceNotFoundException();
		}
		
		try {
			VideoFileManager videoFileManager = VideoFileManager.get();
			Video video = videos.get(id);
			if(videoFileManager.hasVideoData(video)){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				videoFileManager.copyVideoData(video, out);
				return out.toByteArray();
			}else {
				throw new ResourceNotFoundException();
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(); // TODO Return appropriate HTTP error status
		}

	}

	private String getDataUrl(long videoId) {
		String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
		return url;
	}

	private String getUrlBaseForLocalServer() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		String base = "http://"
				+ request.getServerName()
				+ ((request.getServerPort() != 80) ? ":"
						+ request.getServerPort() : "");
		return base;
	}

	public Video save(Video entity) {
		checkAndSetId(entity);
		videos.put(entity.getId(), entity);
		return entity;
	}

	private void checkAndSetId(Video entity) {
		if (entity.getId() == 0) {
			entity.setId(currentId.incrementAndGet());
		}
	}
}
