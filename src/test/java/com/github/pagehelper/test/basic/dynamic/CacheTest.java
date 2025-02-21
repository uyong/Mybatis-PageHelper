/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2022 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.pagehelper.test.basic.dynamic;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.mapper.UserMapper;
import com.github.pagehelper.model.User;
import com.github.pagehelper.util.MybatisHelper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 针对将ms缓存后的测试
 */
public class CacheTest {

    /**
     * 使用Mapper接口调用时，使用PageHelper.startPage效果更好，不需要添加Mapper接口参数
     */
    @Test
    public void testThreads() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        sqlSession.close();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread1 = new Thread(new CacheThread());
        Thread thread2 = new Thread(new CacheThread());
        Thread thread3 = new Thread(new CacheThread());
        Thread thread4 = new Thread(new CacheThread());
        Thread thread5 = new Thread(new CacheThread());
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class CacheThread implements Runnable {
        private CacheThread() {
        }

        public void run() {
            SqlSession sqlSession = MybatisHelper.getSqlSession();
            System.out.println(Thread.currentThread().getId() + "开始运行...");
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            //获取第1页，10条内容，默认查询总数count
            PageHelper.startPage(1, 10);
            List<User> list = userMapper.selectIf2List(Arrays.asList(1, 2), Arrays.asList(3, 4));
            assertEquals(5, list.get(0).getId());
            assertEquals(10, list.size());
            assertEquals(179, ((Page<?>) list).getTotal());

            //获取第1页，10条内容，默认查询总数count
            PageHelper.startPage(1, 10);
            list = userMapper.selectIf2List(Arrays.asList(1, 2), null);
            assertEquals(3, list.get(0).getId());
            assertEquals(10, list.size());
            assertEquals(181, ((Page<?>) list).getTotal());
            sqlSession.close();
        }
    }
}
