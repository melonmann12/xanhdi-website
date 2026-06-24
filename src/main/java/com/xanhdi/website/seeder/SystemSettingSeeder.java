package com.xanhdi.website.seeder;

import com.xanhdi.website.model.SystemSetting;
import com.xanhdi.website.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingSeeder implements CommandLineRunner {

    private final SystemSettingRepository systemSettingRepository;

    @Autowired
    public SystemSettingSeeder(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedSetting("homepage_main_title", "Điểm đến thiết kế riêng cho bạn", "Tiêu đề lớn chính giữa trang chủ");
        seedSetting("homepage_sub_title", "Tận hưởng những trải nghiệm độc quyền tại các địa điểm hoang sơ và yên bình nhất.", "Dòng mô tả ngắn dưới tiêu đề chính");
        seedSetting("homepage_hero_bg", "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=1920", "Link ảnh nền Banner chính của trang chủ");

        // Hero Stats
        seedSetting("hero_stat1_num", "150+", "Thống kê 1 - Số lượng");
        seedSetting("hero_stat1_lbl", "Điểm Đến", "Thống kê 1 - Nhãn");
        seedSetting("hero_stat2_num", "50k", "Thống kê 2 - Số lượng");
        seedSetting("hero_stat2_lbl", "Khách Hàng", "Thống kê 2 - Nhãn");
        seedSetting("hero_stat3_num", "4.9", "Thống kê 3 - Số lượng");
        seedSetting("hero_stat3_lbl", "Đánh Giá", "Thống kê 3 - Nhãn");

        // Tours Header Section
        seedSetting("tours_section_tag", "TOUR", "Section Tour - Thẻ phụ");
        seedSetting("tours_section_title", "Đến đây và khám phá những địa điểm tuyệt đẹp.", "Section Tour - Tiêu đề");
        seedSetting("tours_section_btn", "Đặt ngay", "Section Tour - Nhãn nút");

        // Guides Header Section
        seedSetting("guides_section_tag", "ĐỒI NGŨ", "Section Hướng dẫn viên - Thẻ phụ");
        seedSetting("guides_section_title", "Đội ngũ Xanh Đi đồng hành cùng bạn", "Section Hướng dẫn viên - Tiêu đề");
        seedSetting("guides_section_subtitle", "Những hướng dẫn viên bản địa, chuyên nghiệp và tận tâm, luôn sẵn sàng cùng bạn chinh phục mọi cung đường trải nghiệm.", "Section Hướng dẫn viên - Mô tả");
        seedSetting("guides_section_btn", "Xem tất cả hướng dẫn viên", "Section Hướng dẫn viên - Nhãn nút");

        // Popular Tours Header Section
        seedSetting("popular_section_tag", "KHÁM PHÁ", "Section Tour phổ biến - Thẻ phụ");
        seedSetting("popular_section_title", "Khám phá các tour phổ biến nhất!", "Section Tour phổ biến - Tiêu đề");
        seedSetting("popular_section_subtitle", "Lựa chọn hàng đầu từ những du khách yêu thiên nhiên.", "Section Tour phổ biến - Mô tả");
        seedSetting("popular_section_btn", "Xem tất cả tour", "Section Tour phổ biến - Nhãn nút");

        // Process Section
        seedSetting("process_image_url", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1200", "Section Quy trình - URL ảnh minh họa");
        seedSetting("process_section_tag", "QUY TRÌNH DỄ DÀNG", "Section Quy trình - Thẻ phụ");
        seedSetting("process_section_title", "Quy trình đặt vé", "Section Quy trình - Tiêu đề");
        seedSetting("process_step1_title", "Tìm điểm đến", "Quy trình Bước 1 - Tiêu đề");
        seedSetting("process_step1_content", "Khám phá các lựa chọn đa dạng và chọn nơi bạn muốn đến trải nghiệm.", "Quy trình Bước 1 - Nội dung");
        seedSetting("process_step2_title", "Đặt vé tour", "Quy trình Bước 2 - Tiêu đề");
        seedSetting("process_step2_content", "Lựa chọn ngày giờ phù hợp và đặt chỗ nhanh chóng qua hệ thống của chúng tôi.", "Quy trình Bước 2 - Nội dung");
        seedSetting("process_step3_title", "Thanh toán", "Quy trình Bước 3 - Tiêu đề");
        seedSetting("process_step3_content", "Thanh toán an toàn và bảo mật với nhiều phương thức linh hoạt.", "Quy trình Bước 3 - Nội dung");
        seedSetting("process_step4_title", "Khám phá điểm đến", "Quy trình Bước 4 - Tiêu đề");
        seedSetting("process_step4_content", "Xách ba lô lên và tận hưởng chuyến đi tuyệt vời của bạn cùng Xanh Đi.", "Quy trình Bước 4 - Nội dung");

        // Experience Banner Section
        seedSetting("experience_image_url", "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=1920", "Section Trải nghiệm - URL ảnh nền");
        seedSetting("experience_section_tag", "EXPERIENCE", "Section Trải nghiệm - Thẻ phụ");
        seedSetting("experience_section_title", "Khám phá nơi bạn yêu thích và đặt vé ngay", "Section Trải nghiệm - Tiêu đề");
        seedSetting("experience_section_btn", "Liên hệ để được tư vấn miễn phí", "Section Trải nghiệm - Nhãn nút");

        // Footer Section
        seedSetting("footer_about", "Đừng bỏ lỡ những chương trình khuyến mãi hấp dẫn. Theo dõi mạng xã hội của chúng tôi để cập nhật thông tin mới nhất.", "Footer - Đoạn giới thiệu");
        seedSetting("footer_email", "contact@xanhdi.vn", "Footer - Email liên hệ");
        seedSetting("footer_phone", "+84 1800 888 888", "Footer - Số điện thoại liên hệ");
        seedSetting("footer_copyright", "© 2024 Xanh Đi. Tất cả quyền được bảo lưu.", "Footer - Dòng bản quyền");
    }

    private void seedSetting(String key, String value, String description) {
        if (!systemSettingRepository.existsById(key)) {
            SystemSetting setting = new SystemSetting(key, value, description);
            systemSettingRepository.save(setting);
            System.out.println("Seeded system setting: " + key + " -> " + value);
        }
    }
}
