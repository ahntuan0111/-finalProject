package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Question;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuestionResponse {
    private Long id;
    private String explanation;
    private Integer orderNo;
    // Thay vì dùng Entity, ta dùng các list DTO ẩn hoặc map sang dạng thô
    private List<QuestionContentDto> contents;
    private List<ChoiceDto> choices;

    // Cấu trúc DTO con nội bộ (để code gọn, bạn có thể tách ra file riêng nếu muốn)
    @Data
    public static class QuestionContentDto {
        private Long id;
        private String contentType;
        private String contentValue;
        private Integer contentOrder;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentValue() {
            return contentValue;
        }

        public void setContentValue(String contentValue) {
            this.contentValue = contentValue;
        }

        public Integer getContentOrder() {
            return contentOrder;
        }

        public void setContentOrder(Integer contentOrder) {
            this.contentOrder = contentOrder;
        }
    }

    @Data
    public static class ChoiceDto {
        private Long id;
        private String contentType;
        private String contentValue;
        private Boolean correct;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Boolean getCorrect() {
            return correct;
        }

        public void setCorrect(Boolean correct) {
            this.correct = correct;
        }

        public String getContentValue() {
            return contentValue;
        }

        public void setContentValue(String contentValue) {
            this.contentValue = contentValue;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }

    // Hàm map từ Entity Question sang QuestionResponse (Cắt đứt liên kết tới Quiz)
    public static QuestionResponse fromEntity(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setExplanation(question.getExplanation());
        response.setOrderNo(question.getOrderNo());

        if (question.getContents() != null) {
            response.setContents(question.getContents().stream().map(c -> {
                QuestionContentDto dto = new QuestionContentDto();
                dto.setId(c.getId());
                dto.setContentType(c.getContentType().name());
                dto.setContentValue(c.getContentValue());
                dto.setContentOrder(c.getContentOrder());
                return dto;
            }).collect(Collectors.toList()));
        }

        if (question.getChoices() != null) {
            response.setChoices(question.getChoices().stream().map(ch -> {
                ChoiceDto dto = new ChoiceDto();
                dto.setId(ch.getId());
                dto.setContentType(ch.getContentType().name());
                dto.setContentValue(ch.getContentValue());
                dto.setCorrect(ch.getCorrect());
                return dto;
            }).collect(Collectors.toList()));
        }

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public List<QuestionContentDto> getContents() {
        return contents;
    }

    public void setContents(List<QuestionContentDto> contents) {
        this.contents = contents;
    }

    public List<ChoiceDto> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceDto> choices) {
        this.choices = choices;
    }
}