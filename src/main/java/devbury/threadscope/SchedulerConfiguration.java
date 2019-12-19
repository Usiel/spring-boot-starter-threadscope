/*
 * Copyright 2015 devbury LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package devbury.threadscope;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@ConditionalOnMissingBean(AsyncConfigurer.class)
@EnableAsync
public class SchedulerConfiguration implements AsyncConfigurer {

    @Autowired
    private ThreadScopeManager threadScopeManager;

    @Autowired
    private ThreadScopeProperties threadScopeProperties;

    @Autowired
    private ThreadScopePropagatingScheduler taskExecutor;

    @Autowired
    private AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler;

    @Bean
    @Primary
    @ConditionalOnMissingBean(ThreadScopePropagatingScheduler.class)
    public ThreadScopePropagatingScheduler defaultThreadScopePropagatingScheduler() {
        ThreadScopePropagatingScheduler threadScopePropagatingScheduler = new ThreadScopePropagatingScheduler
                (threadScopeManager);
        threadScopePropagatingScheduler.setPoolSize(threadScopeProperties.getPoolSize());
        threadScopePropagatingScheduler.setThreadNamePrefix(threadScopeProperties.getThreadNamePrefix());
        return threadScopePropagatingScheduler;
    }

    @Bean
    @ConditionalOnMissingBean(AsyncUncaughtExceptionHandler.class)
    public AsyncUncaughtExceptionHandler defaultAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Override
    public Executor getAsyncExecutor() {
        return this.taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncUncaughtExceptionHandler;
    }
}
