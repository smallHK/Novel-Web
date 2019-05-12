package com.hk.service;

import com.hk.entity.Editor;
import com.hk.entity.Profile;
import com.hk.repository.EditorRepository;
import com.hk.repository.ProfileRepository;
import org.springframework.stereotype.Service;

/**
 * @author smallHK
 * 2019/5/11 23:48
 */
@Service
public class EditorService {

    private EditorRepository editorRepository;

    private ProfileRepository profileRepository;

    public EditorService(EditorRepository editorRepository,
                         ProfileRepository profileRepository) {
        this.editorRepository = editorRepository;
        this.profileRepository = profileRepository;
    }

    public Profile findProfileByEditorId(Integer editorId) {
        Editor editor = editorRepository.findById(editorId).orElseThrow();
        return profileRepository.findById(editor.getProfileId()).orElseThrow();
    }


}
