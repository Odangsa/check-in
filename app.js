const fs = require("fs");
const path = require("path");
const express = require("express");
const app = express();

const PORT = 3000; // 포트 상수를 위로 이동

app.use(express.json()); // JSON 파싱을 위해 추가

app.use((req, res, next) => {
  console.log(`${req.method} ${req.url}`);
  next();
});

app.get("/", (req, res) => {
  res.send("Hello World!");
});

// JSON 파일 경로 설정
const bookDetailsFilePath = path.join(__dirname, "data/book_detail.json");
const recommendationBookTwoFilePath = path.join(
  __dirname,
  "data/recommendation_book_two.json"
);
const recommendationsTodayBookFilePath = path.join(
  __dirname,
  "data/recommendations_today_book.json"
);
const libFilePath = path.join(__dirname, "data/recent_libraries.json");
const stampBoardFilePath = path.join(__dirname, "data/stamp_board.json");

// JSON 파일 읽기 함수
const readJSONFile = (filePath) => {
  try {
    const data = fs.readFileSync(filePath, "utf-8");
    return JSON.parse(data);
  } catch (error) {
    console.error(`Error reading file from disk: ${error}`);
    return null;
  }
};

// 파일에서 도서 데이터를 읽어오는 함수 (수정됨)
function readBookDetails() {
  try {
    const data = fs.readFileSync(bookDetailsFilePath, "utf8");
    return JSON.parse(data);
  } catch (error) {
    console.error("Error reading book details file:", error);
    return null;
  }
}

// API 엔드포인트: ISBN으로 도서 상세 정보 제공 (위치 정보 포함)
app.get("/api/book_details/:ISBN", (req, res) => {
  const { ISBN } = req.params;
  const { latitude, longitude } = req.query;

  if (!ISBN) {
    return res.status(400).json({ error: "ISBN is required" });
  }

  const bookDetails = readBookDetails();
  if (!bookDetails) {
    return res.status(500).json({ error: "Could not read book details file" });
  }

  // 파일에 있는 유일한 도서 데이터 사용
  const book = bookDetails;

  // 도서관 정보에서 null 값을 가진 필드 제거
  book.libs = book.libs.map((lib) => {
    return Object.fromEntries(
      Object.entries(lib).filter(([key, value]) => value !== null)
    );
  });

  res.json(book);
  console.log(
    "Book details sent successfully for ISBN:",
    ISBN,
    "Location received:",
    latitude,
    longitude
  );
});

// JSON 파일 경로 설정에 추가

// API 엔드포인트: 최근 방문한 도서관 정보 제공
app.get("/api/recent_libraries/:userId", (req, res) => {
  const { userId } = req.params;
  const libData = readJSONFile(libFilePath);
  if (libData && libData.recent_libraries) {
    const userLibraries = libData.recent_libraries.slice(0, 3); // 최근 3개만 반환
    console.log(
      "Recent libraries data:",
      JSON.stringify(userLibraries, null, 2)
    );
    res.json(userLibraries);
  } else {
    console.log("Failed to read recent libraries data");
    res
      .status(500)
      .json({ error: "Could not read recent libraries data file" });
  }
});

// API 엔드포인트: 메인 페이지 추천 도서 제공 (recommendation_book_two)
app.get("/api/recommendation_book_two", (req, res) => {
  const recommendationBooks = readJSONFile(recommendationBookTwoFilePath);
  if (recommendationBooks) {
    console.log(
      "Recommendation books:",
      JSON.stringify(recommendationBooks, null, 2)
    );
    res.json(recommendationBooks);
    console.log("Recommendation books sent successfully");
  } else {
    console.log("Failed to read recommendation books");
    res.status(500).json({ error: "Could not read recommendation books file" });
  }
});

// API 엔드포인트: 오늘의 추천 도서 제공 (recommendations_today_book)
app.get("/api/recommendations_today_book", (req, res) => {
  const todayRecommendations = readJSONFile(recommendationsTodayBookFilePath);
  if (todayRecommendations) {
    console.log(
      "Today recommendations:",
      JSON.stringify(todayRecommendations, null, 2)
    );
    res.json(todayRecommendations);
    console.log("Today recommendations sent successfully");
  } else {
    console.log("Failed to read today recommendations");
    res
      .status(500)
      .json({ error: "Could not read today recommendations file" });
  }
});

// API 엔드포인트: 스탬프 보드 정보 제공
app.get("/api/stamp_board/:userId", (req, res) => {
  const { userId } = req.params;
  const stampBoardData = readJSONFile(stampBoardFilePath);
  if (stampBoardData) {
    // 여기서 userId를 사용하여 해당 사용자의 스탬프 보드 정보를 필터링할 수 있습니다.
    // 예시로 모든 데이터를 반환하고 있지만, 실제로는 사용자별 데이터를 반환해야 합니다.
    console.log("Stamp board data:", JSON.stringify(stampBoardData, null, 2));
    res.json(stampBoardData);
  } else {
    console.log("Failed to read stamp board data");
    res.status(500).json({ error: "Could not read stamp board data file" });
  }
});

// 서버 시작 (여기서만 listen 호출)
app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
