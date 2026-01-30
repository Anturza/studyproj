package mod2;

import mod2.Entities.Name;
import mod2.Entities.User;
import mod2.dao.UserDaoImpl;
import mod2.validation.UserValidator;
import org.hibernate.cfg.Configuration;

import java.time.LocalDateTime;
import java.util.List;

public class TryHibernate {
    public static void main(String[] args) {
        Configuration userConfiguration = new org.hibernate.cfg.Configuration()
                .addAnnotatedClass(User.class).addAnnotatedClass(Name.class);


        Name name1 = new Name("Ololosha", "Ololoev", "Ololoevich");
        Name name2 = new Name("Krombacher", "Waltz");
        Name name3 = new Name("Vlad");
        Name name4 = new Name("Dimitry", "Voronov");

        User user1 = new User(name1, "ggg@aaa.ru", 33, LocalDateTime.now());
        User user2 = new User(name2, "fff@sss.de", 18, LocalDateTime.now());
        User user3 = new User(name3, "asd@asd.com", 28, LocalDateTime.now());
        User user4 = new User(name4, "qwe@qwe.org", 42, LocalDateTime.now());
        User user = new User(new Name("O"), "qwe@qwe.org", 42, LocalDateTime.now());

        UserDaoImpl userDao = new UserDaoImpl(userConfiguration.buildSessionFactory());

            //CRUD ops
//        userDao.create(user1);
//        userDao.create(user2);
//        userDao.create(user3);
//        userDao.create(user4);
//        User someUser = userDao.get(5L);
//        someUser.setEmail("abracadabra");
//        userDao.update(someUser);

        List<User> users = userDao.getAll();
        for (User us : users) {
            System.out.println(us);
        }
//        User someUser = userDao.get(4L);
//        someUser.setEmail("fluegegeheiemen@nl.com");
//        userDao.update(someUser);
//        System.out.println(userDao.get(4L));
//
//        someUser = userDao.get(2L);
//        someUser.setName(new Name("Comrade"));
//        userDao.update(someUser);
//        System.out.println(userDao.get(2L));

//        userDao.remove(3L);

//        User currUser = userDao.get(3L);
//        System.out.println(currUser);
//        currUser.setEmail("vahvah@ff.com");
//        userDao.update(currUser);
//        currUser = userDao.get(4L);
//        currUser.setName(new Name("Ivanov", "Ivan", "Ivanovich"));
//        userDao.update(currUser);

//        userDao.remove(1L);
//        userDao.remove(3L);
//        userDao.remove(4L);







    }
}
