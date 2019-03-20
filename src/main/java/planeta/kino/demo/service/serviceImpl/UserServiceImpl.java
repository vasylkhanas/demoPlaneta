package planeta.kino.demo.service.serviceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import planeta.kino.demo.DTO.UserFullDTO;
import planeta.kino.demo.DTO.UserSimpleDTO;
import planeta.kino.demo.entity.User;
import planeta.kino.demo.entity.UserInfo;
import planeta.kino.demo.repository.UserInfoRepository;
import planeta.kino.demo.repository.UserRepository;
import planeta.kino.demo.service.UserService;
import planeta.kino.demo.utils.ObjectMapperUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager
            .getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapperUtils modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public User save(UserSimpleDTO userSimpleDTO) throws Exception {
        User user = modelMapper.map(userSimpleDTO, User.class);
        List<User> userWithSameCredentials = userRepository
                .getAllByEmailOrPassword(user.getEmail(), user.getPassword());
        if(!userWithSameCredentials.isEmpty()) {
            throw new Exception("Credentials are busy. Please, try one more time " +
                    "with other email or password");
        }
        return userRepository.save(user);
    }

    @Override
    public User getOne(UserSimpleDTO userSimpleDTO) {
        List<User> usersWithSuchMail = userRepository.
                findByEmail(userSimpleDTO.getEmail());
        for(User u : usersWithSuchMail) {
            if(passwordEncoder.matches(userSimpleDTO.getPassword(), u.getPassword())) {
                return u;
            }
        }
        throw new IllegalArgumentException();
    }

}
