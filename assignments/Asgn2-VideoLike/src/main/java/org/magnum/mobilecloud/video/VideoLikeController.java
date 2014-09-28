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

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}
	
	@ResponseBody
	@RequestMapping(value=VideoApi.VIDEO, method= RequestMethod.GET)
	public Collection<Video> getVideos(){
		return Lists.newArrayList(videos.findAll());
	}
	
	@RequestMapping(value=VideoApi.VIDEO, method= RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video){
		video.setLikes(0);
		Video v = videos.save(video);
		return v;
	}
	
	@RequestMapping(value=VideoApi.VIDEO + "/{id}")
	public @ResponseBody Video getVideo(@RequestParam(value = "id") Long id){
		return videos.findOne(id);
	}
	
	@RequestMapping(value = VideoApi.VIDEO + "/{id}/" + VideoApi.LIKE_PARAMETER, method = RequestMethod.POST)
	public void likeVideo(@RequestParam(value = "id") Long id) {
		Video video = videos.findOne(id);
		video.setLikes(video.getLikes() + 1);
	}
	
	@RequestMapping(value = VideoApi.VIDEO + "/{id}/" + VideoApi.UNLIKE_PARAMETER, method = RequestMethod.POST)
	public void unlikeVideo(@RequestParam(value = "id") Long id) {

	}
	
}