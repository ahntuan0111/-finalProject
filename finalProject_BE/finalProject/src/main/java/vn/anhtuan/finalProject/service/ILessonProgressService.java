package vn.anhtuan.finalProject.service;

import vn.anhtuan.finalProject.dto.request.LessonCompletionRequest;
import vn.anhtuan.finalProject.dto.response.ProgressResponse;
import vn.anhtuan.finalProject.dto.response.SubjectOverviewResponse;

import java.util.List;

public interface ILessonProgressService {
    void markAsCompleted(LessonCompletionRequest request);

    List<ProgressResponse> getMyProgress(Integer gradeId);

    SubjectOverviewResponse getSubjectOverview(String subjectCode, Integer gradeId);

}
