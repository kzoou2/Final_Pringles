package project.finalpj.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.finalpj.entity.Member;
import project.finalpj.repository.MemberRepository;
import project.finalpj.service.MemberService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> map) {
        try {
            System.out.println("들어오나?");
            memberService.create(map.get("userid"), map.get("password"), map.get("username"), map.get("email"));
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("이미 등록된 사용자입니다");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("결론은 에러");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> map) {
        try {
            String userid = map.get("userid");
            String password = map.get("password");

            // 로그인 처리 (비밀번호 검증)
            boolean loginSuccess = memberService.login(userid, password);

            if (loginSuccess) {
//                return ResponseEntity.ok().build();
                Optional<Member> member = memberRepository.findByUserid(userid);

                LoginInformation loginInformation = null;
                if (member.isPresent()) {
                    Member m = member.get();
                    String username = m.getUsername();

                    loginInformation = new LoginInformation();
                    loginInformation.setUserid(userid);
                    loginInformation.setUsername(username);
                }

                return new ResponseEntity<>(loginInformation, HttpStatus.OK);
            } else {
                return ResponseEntity.badRequest().body("로그인 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로그인 에러");
        }
    }

    @Getter
    @Setter
    @ToString
    static class LoginInformation {
        private String username;
        private String userid;
    }


    @GetMapping("/getUserName")
    public ResponseEntity<?> getUserName(@RequestParam String userId) {
        try {
            //userId를 사용하여 사용자 이름 조회
            String userName = memberService.getUserName(userId);

            if (userName != null) {
                return ResponseEntity.ok(userName);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("사용자 이름 조회 에러");
        }
    }
}