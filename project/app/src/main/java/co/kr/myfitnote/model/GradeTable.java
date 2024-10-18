package co.kr.myfitnote.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 점수 테이블
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeTable {

    private String gender;
    private int minAge;
    private int maxAge;
    private int grade1Count;
    private int grade2Count;
    private int grade3Count;

}
