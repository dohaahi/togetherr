package together.together_project.service;

import com.password4j.Password;
import org.springframework.stereotype.Service;

@Service
public class BcryptService {

    public String encodeBcrypt(String plainPassword) {
        return Password.hash(plainPassword).withBcrypt().getResult();
    }

    public boolean matchBcrypt(String plainPassword, String hashPassword) {
        return Password.check(plainPassword, hashPassword).withBcrypt();
    }
}