package com.wannistudio.security.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Test
    @WithAnonymousUser
    void index_anonymous() throws Exception {
        mockMvc.perform(get("/")
//                .with(anonymous())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUser
    void index_user() throws Exception {
        mockMvc.perform(get("/")
//                .with(user("wanni")
//                        .roles("USER"))
        ) // wanni로 로그인 되어있는 index 페이지(DB에 존재x)
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    void index_admin() throws Exception {
        mockMvc.perform(get("/").with(user("wanni").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    void admin_user() throws Exception {
        mockMvc.perform(get("/admin").with(user("wanni").roles("USER"))
        ).andDo(print())
            .andExpect(status().isForbidden())
        ;
    }

    @Test
    void login_success() throws Exception {
        Account account = createUser();

        mockMvc.perform(formLogin().user(account.getUsername()).password("123"))
                .andExpect(authenticated())
        ;
    }

    @Test
    void login_fail() throws Exception {
        Account account = createUser();

        mockMvc.perform(formLogin().user(account.getUsername()).password("1223"))
                .andExpect(unauthenticated())
        ;
    }

    private Account createUser() {
        Account account = new Account();
        account.setUsername("wanni");
        account.setPassword("123");
        account.setRole("USER");
        return accountService.createNewAccount(account);
    }
}