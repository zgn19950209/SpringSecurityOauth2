package io.spring.auth.center.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @Description: 认证中心配置类
 * @Created 2022/10/12 1:11
 **/
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Resource
    private TokenStore tokenStore;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private ClientDetailsService clientDetailsService;

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices() {

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(jwtAccessTokenConverter));

        DefaultTokenServices services = new DefaultTokenServices();
        services.setTokenEnhancer(tokenEnhancerChain);
        services.setClientDetailsService(clientDetailsService);
        services.setSupportRefreshToken(true);
        services.setTokenStore(tokenStore);
        services.setAccessTokenValiditySeconds(7200);
        services.setRefreshTokenValiditySeconds(259200);
        return services;
    }


    /**
     * 用来配置令牌端点的安全约束
     *
     * @param security a fluent configurer for security features
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        /**
         * （1）tokenKey这个endpoint当使用JwtToken且使用非对称加密时，资源服务用于获取公钥而开放的，这里指这个endpoint完全公开。
         * （2）checkToken这个endpoint完全公开
         * （3） 允许表单认证
         */
        security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()").allowFormAuthenticationForClients();

    }

    /**
     * 用来配置客户端详情服务（ClientDetailsService），客户端详情信息在这里进行初始化，你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息
     *
     * @param clients the client details configurer
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /**
         * clientId ：（必须的）用来标识客户的Id。
         * secret ：（需要值得信任的客户端）客户端安全码，如果有的话。
         * scope ：用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围。
         * authorizedGrantTypes ：此客户端可以使用的授权类型，默认为空。
         * authorities ：此客户端可以使用的权限（基于Spring Security authorities）
         */
        /**
         * 客户端详情（Client Details）能够在应用程序运行的时候进行更新，可以通过访问底层的存储服务
         * （例如将客户端详情存储在一个关系数据库的表中，就可以使用 JdbcClientDetailsService）
         * 或者通过自己实现ClientRegistrationService接口（同时你也可以实现 ClientDetailsService 接口）来进行管理。
         */
        clients.inMemory()                //使用in‐memory存储
                .withClient("c1")
                .secret(new BCryptPasswordEncoder().encode("secret"))//$2a$10$0uhIO.ADUFv7OQ/kuwsC1.o3JYvnevt5y3qX/ji0AUXs4KYGio3q6
                .resourceIds("r1")
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")//该client允许的授权类型
                .scopes("all")            //授权范围
                .autoApprove(false)
                .redirectUris("https://www.baidu.com");
        System.out.println("clients = " + clients);
    }


    /**
     * 用来配置令牌（token）的访问端点和令牌服务(tokenServices)
     *
     * @param endpoints the endpoints configurer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /**
         * --------------------------------------------------授权类型----------------------------------------------------
         *
         * 1.authenticationManager ：
         * 认证管理器，当你选择了资源所有者密码（password）授权类型的时候，请设置这个属性注入一个 AuthenticationManager 对象。
         *
         * 2.userDetailsService ：
         *     如果你设置了这个属性的话，那说明你有一个自己的 UserDetailsService 接口的实现，或者你可以把这个东西设置到全局域上面去（例如 GlobalAuthenticationManagerConfigurer 这个配置对象），
         * 当你设置了这个之后，那么 "refresh_token" 即刷新令牌授权类型模式的流程中就会包含一个检查，用来确保这个账号是否仍然有效，假如说你禁用了这个账户的话。
         *
         * 3.authorizationCodeServices ：
         * 这个属性是用来设置授权码服务的（即 AuthorizationCodeServices 的实例对象），主要用于 "authorization_code" 授权码类型模式。
         *
         * 4.implicitGrantService ：
         * 这个属性用于设置隐式授权模式，用来管理隐式授权模式的状态。
         *
         * 5.tokenGranter ：
         *     当你设置了这个东西（即 TokenGranter 接口实现），那么授权将会交由你来完全掌控，并且会忽略掉上面的这几个属性，这个属性一般是用作拓展用途的，
         * 即标准的四种授权模式已经满足不了你的需求的时候，才会考虑使用这个。
         */

        /**
         * --------------------------------------------------授权端点----------------------------------------------------
         * AuthorizationServerEndpointsConfigurer 这个配置对象有一个叫做 pathMapping() 的方法用来配置端点URL链接，它有两个参数：
         *
         * 第一个参数： String 类型的，这个端点URL的默认链接。
         * 第二个参数： String 类型的，你要进行替代的URL链接。
         * 以上的参数都将以 "/" 字符为开始的字符串，框架的默认URL链接如下列表，可以作为这个 pathMapping() 方法的第一个参数：
         *
         * /oauth/authorize ：授权端点。
         * /oauth/token ：令牌端点。
         * /oauth/confirm_access ：用户确认授权提交端点。
         * /oauth/error ：授权服务错误信息端点。
         * /oauth/check_token ：用于资源服务访问的令牌解析端点。
         * /oauth/token_key ：提供公有密匙的端点，如果你使用JWT令牌的话。
         */
        endpoints.authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices())
                .tokenServices(tokenServices())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST).pathMapping("/oauth/confirm_access", "/custom/confirm_access")
                .reuseRefreshTokens(false);
    }


}
