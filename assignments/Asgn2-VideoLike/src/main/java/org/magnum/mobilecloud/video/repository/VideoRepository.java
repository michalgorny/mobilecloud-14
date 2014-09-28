package org.magnum.mobilecloud.video.repository;

import java.util.Collection;

import org.magnum.mobilecloud.video.VideoApi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = VideoApi.VIDEO)
public interface VideoRepository extends CrudRepository<Video, Long> {

	public Collection<Video> findByName(@Param(VideoApi.TITLE_PARAMETER) String title);
	
	public Collection<Video> findByDurationLessThan(@Param(VideoApi.DURATION_PARAMETER) long maxduration);
}
