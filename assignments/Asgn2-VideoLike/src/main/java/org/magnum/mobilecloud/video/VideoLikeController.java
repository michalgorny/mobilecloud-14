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

package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;

import org.magnum.mobilecloud.video.exceptions.AlreadyLikedException;
import org.magnum.mobilecloud.video.exceptions.NotLikedYetException;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class VideoLikeController {
	
	@Autowired
	private VideoRepository videos;

	@ResponseBody
	@RequestMapping(value=VideoApi.VIDEO_PATH, method= RequestMethod.GET)
	public Collection<Video> getVideos(){
		return Lists.newArrayList(videos.findAll());
	}
	
	@RequestMapping(value=VideoApi.VIDEO_PATH, method= RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video){
		video.setLikes(0);
		Video v = videos.save(video);
		return v;
	}
	
	@RequestMapping(value=VideoApi.VIDEO_PATH + "/{id}")
	public @ResponseBody Video getVideo(@PathVariable(value = "id") Long id){
		return getVideoFromRepository(id);
	}
	
	@RequestMapping(value = VideoApi.LIKE_PATH, method = RequestMethod.POST)
	public ResponseEntity<Void> likeVideo(@PathVariable("id") Long id, Principal p) {
		Video video = getVideoFromRepository(id);
		
		try {
			video.addLike(p.getName());
			videos.save(video);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AlreadyLikedException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = VideoApi.UNLIKE_PATH, method = RequestMethod.POST)
	public ResponseEntity<Void> unlikeVideo(@PathVariable(value = "id") Long id, Principal p) {
		Video video = getVideoFromRepository(id);
		
		try {
			video.unlike(p.getName());
			videos.save(video);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (NotLikedYetException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = VideoApi.LIKED_BY_PATH)
	@ResponseBody
	public Collection<String> likedBy(@PathVariable(value = "id") Long id) {
		Video video = getVideoFromRepository(id);
		return video.getUserLiked();
	}
	
	private Video getVideoFromRepository(Long id) {
		Video video = videos.findOne(id);
		if (video == null) {
			throw new ResourceNotFoundException("Video with id " + id + " not found");
		}
		return video;
	}
	
	@RequestMapping(value = VideoApi.FIND_BY_NAME_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(@RequestParam(value = VideoApi.TITLE_PARAMETER) String name) {
		return videos.findByName(name);
	}
	
	@RequestMapping(value = VideoApi.FIND_BY_DURATION_LESS_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDuration(@RequestParam(value = VideoApi.DURATION_PARAMETER) Long duration) {
		return videos.findByDurationLessThan(duration);
	}
	
}
