package org.magnum.mobilecloud.video.repository;

import java.util.Collection;

import org.magnum.mobilecloud.video.VideoApi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long> {

	public Collection<Video> findByName(@Param(VideoApi.TITLE_PARAMETER) String name);
	
	public Collection<Video> findByDurationLessThan(@Param(VideoApi.DURATION_PARAMETER) long maxduration);
}
