package io.spring.resource.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

import static io.spring.resource.server.constant.CommonConstant.RESOURCE_ID;


/**
 * @Description: 资源服务器配置
 * @Created 2022/10/12 15:20
 **/
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    @Resource
    private TokenStore tokenStore;

    @Bean
    public ResourceServerTokenServices resourceServerTokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://127.0.0.1:30000/oauth/check_token");
        remoteTokenServices.setClientId("c1");
        remoteTokenServices.setClientSecret("secret");
        return remoteTokenServices;
    }

    /**
     *  核心配置
     *
     * @param resources configurer for the resource server
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        /**
         * resourceId方法标志了该服务的id，需要和在auth-center服务中配置的id一致。
         * tokenServices方法指定了令牌管理的实例
         */
        resources
                .resourceId(RESOURCE_ID)
//                .tokenServices(resourceServerTokenServices)//令牌服务
                .tokenStore(tokenStore)
                .stateless(true);
    }


    /**
     * Auth2.0安全配置
     *
     * @param http the current http filter configuration
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        /**
         * 该配置和下面的Web安全配置很像，但是不一样，这里仅仅对auth2.0的安全进行配置。
         * 这里的.antMatchers("/**").access("#oauth2.hasScope('all')")表示所有的请求携带的令牌都必须拥有all的授权范围，
         * 其中all授权范围必须和认证服务中的配置相一致。
         */
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").access("#oauth2.hasScope('all')")
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }


}
