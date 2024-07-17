package com.pinkproject.notice;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveNoticeAdminRecord;
import com.pinkproject.admin.SessionAdmin;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NoticeServiceTest {

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private NoticeRepository noticeRepository;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private HttpSession session;

    @Autowired
    private NoticeService noticeService;

    private Admin admin;
    private Notice notice1;
    private Notice notice2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        admin = Admin.builder()
                .id(1)
                .username("admin")
                .password("password") // 패스워드 값을 설정합니다.
                .build();
        admin = entityManager.merge(admin);

        notice1 = Notice.builder()
                .id(1)
                .title("notice1")
                .content("content1")
                .admin(admin)
                .createdAt(LocalDateTime.now())
                .build();

        notice2 = Notice.builder()
                .id(2)
                .title("notice2")
                .content("content2")
                .admin(admin)
                .createdAt(LocalDateTime.now())
                .build();

        notice1 = entityManager.merge(notice1);
        notice2 = entityManager.merge(notice2);
        entityManager.flush();
    }

    @Test
    @Transactional
    public void getAllNotices_test() {
        List<Notice> notices = Arrays.asList(notice1, notice2);
        when(noticeRepository.findAll()).thenReturn(notices);

        // Mock된 데이터 확인
        System.out.println("Mock된 Notice 리스트: " + notices);

        List<_DetailNoticeAdminRecord> result = noticeService.getAllNotices();

        // 변환된 데이터 확인
        System.out.println("결과 리스트: " + result);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("notice1", result.get(0).title());
        Assertions.assertEquals("notice2", result.get(1).title());
        Assertions.assertEquals("content1", result.get(0).content());
        Assertions.assertEquals("content2", result.get(1).content());
        Assertions.assertEquals("admin", result.get(0).username());
        Assertions.assertEquals("admin", result.get(1).username());
    }

    @Test
    @Transactional
    public void searchNotices_test() {

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Notice> noticePage = new PageImpl<>(Arrays.asList(notice1, notice2));
        when(noticeRepository.findByKeywordWithNotice("notice", pageRequest)).thenReturn(noticePage);

        Page<_DetailNoticeAdminRecord> result = noticeService.searchNotices("notice", 0);

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("notice1", result.getContent().get(0).title());
        Assertions.assertEquals("notice2", result.getContent().get(1).title());
    }

    @Test
    @Transactional
    public void getNotices_test() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Notice> noticePage = new PageImpl<>(Arrays.asList(notice1, notice2));
        when(noticeRepository.findAllWithAdmin(pageRequest)).thenReturn(noticePage);

        Page<_DetailNoticeAdminRecord> result = noticeService.getNotices(0);

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("notice1", result.getContent().get(0).title());
        Assertions.assertEquals("notice2", result.getContent().get(1).title());
    }

    @Test
    @Transactional
    public void detailNoticeAdminRecord_test() {
        when(noticeRepository.findById(1)).thenReturn(Optional.of(notice1));

        _DetailNoticeAdminRecord result = noticeService.detailNoticeAdminRecord(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("notice1", result.title());
        Assertions.assertEquals("content1", result.content());
        Assertions.assertEquals("admin", result.username());
    }

    @Test
    @Transactional
    public void saveNotice_test() {
        _SaveNoticeAdminRecord saveNoticeAdminRecord = new _SaveNoticeAdminRecord("notice1", "content1");
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice1);

        Integer result = noticeService.saveNotice(saveNoticeAdminRecord, admin);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(notice1.getId(), result);
    }

    @Test
    @Transactional
    public void getNoticeById_test() {
        when(noticeRepository.findById(1)).thenReturn(Optional.of(notice1));

        _DetailNoticeAdminRecord result = noticeService.getNoticeById(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("notice1", result.title());
    }

    @Test
    @Transactional
    public void createNotice_test() {
        _SaveNoticeAdminRecord request = new _SaveNoticeAdminRecord("notice1", "content1");
        when(session.getAttribute("admin")).thenReturn(new SessionAdmin(admin));
        when(adminRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice1);

        Notice result = noticeService.createNotice(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("notice1", result.getTitle());
    }

    @Test
    @Transactional
    public void deleteNotice_test() {
        when(session.getAttribute("admin")).thenReturn(new SessionAdmin(admin));
        when(adminRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(noticeRepository.findById(notice1.getId())).thenReturn(Optional.of(notice1));

        Assertions.assertDoesNotThrow(() -> noticeService.deleteNotice(notice1.getId()));
    }
}
