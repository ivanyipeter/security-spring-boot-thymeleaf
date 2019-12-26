package hu.hibridlevel.test.mvctest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
//@EnableJpaRepositories(basePackages = "hu.hibridlevel.test.mvctest.repository")
@PropertySource("classpath:application-test.properties")
//@ComponentScan(basePackageClasses = {UserRepository.class, UserService.class, RoleRepository.class, RoleService.class})
//@EntityScan(basePackageClasses = {User.class, Role.class})
@EnableAutoConfiguration
//@EnableTransactionManagement
public class TestConfig {

//    @Bean
//    @Profile("test")
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("sa");
//        return dataSource;
//    }
}
