package com.hk.repository.event;

import com.hk.entity.event.VolumePublishEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/3 22:27
 */
public interface VolumePublishEventRepo extends CrudRepository<VolumePublishEvent, Integer> {

    List<VolumePublishEvent> findAllByEditorIdAndStatus(Integer editorId, Integer status);

    void deleteAllByVolumeId(List<Integer> volumeIds);
}
