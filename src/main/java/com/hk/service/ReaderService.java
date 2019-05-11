package com.hk.service;

import com.hk.entity.Reader;
import com.hk.repository.ReaderRepo;
import org.springframework.stereotype.Service;

/**
 * 读者服务bean
 *
 * @author smallHK
 * 2019/5/8 16:12
 */
@Service
public class ReaderService {

    private ReaderRepo readerRepo;

    public ReaderService(ReaderRepo readerRepo) {
        this.readerRepo = readerRepo;
    }

    /**
     * 用户注册
     */
    public void register(String username, String password) {
        Reader reader = new Reader();
        reader.setUsername(username);
        reader.setPassword(password);
        readerRepo.save(reader);
    }

    /**
     * 登陆验证
     * 用户账户密码正确，返回reader
     *  否则返回null
     */
    public Reader readerLogin(String username, String password) {

        Iterable<Reader> readers = readerRepo.findAll();
        for (Reader reader : readers) {
            if (reader.getUsername().equals(username) && reader.getPassword().equals(password)) {
                return reader;
            }
        }
        return null;
    }

}
