# Network Communication Lab — complete Java project

مشروع كامل مبني على **الكود الأصلي داخل network-communication-master.zip** مع استكمال
`WebClient` و`Aggregator` والإبقاء على مساري `/task` و`/status`، وإضافة `/api/analyze`
لتجربة JSON المطلوبة في ملف المخبر، بالإضافة إلى تجربتي Java Serialization وProtocol Buffers.

## المتطلبات على Windows / VS Code

- Java JDK 25 أو أحدث، بحيث يعمل `java -version` و`javac -version`.
- Apache Maven 3.9 أو أحدث بحيث يعمل `mvn -version`، مع الإنترنت **أول مرة** لتنزيل اعتماد `protobuf-java` وأدوات بناء Maven.
- VS Code مع إضافة **Extension Pack for Java** (اختيارية لكنها تسهّل فتح Maven).
- لا تحتاج إلى تثبيت `protoc` لتشغيل الملفات المولدة **المضمّنة مسبقاً**. لإعادة توليدها تحتاج protoc 3.x أو ما يتوافق معه.

## الخطوات المختصرة (PowerShell)

1. فك الضغط وافتح المجلد `Network_Communication_Complete` بالكامل في VS Code.
2. افتح **Terminal > New Terminal** وتأكد أنك في نفس المجلد الذي فيه `pom.xml` الرئيسي.
3. شغّل الأمر:

```powershell
powershell -ExecutionPolicy Bypass -File .\run_all.ps1
```

السكريبت يبني الوحدات، ويشغّل خادمين على `8080` و`8081`، ويمرّر أمثلة HTTP وJSON
وJava Serialization وProtobuf، ثم يحفظ المخرجات داخل `results/run_results.txt`.
يُغلق السكريبت الخادمين اللذين أنشأهما بعد الاختبارات. إذا كانت المنافذ مشغولة، أغلق التطبيق الذي يشغلها أولاً.

إذا لم تتوفر Maven أو الإنترنت، يمكن تشغيل **جزء Java العادي وHTTP** مباشرة بـ `javac`؛
أما Protobuf فيحتاج مكتبة `protobuf-java` عند التجميع والتشغيل.

## التشغيل اليدوي

```powershell
mvn clean package
mvn -pl lab dependency:copy-dependencies -DoutputDirectory=target/dependency
```

في ثلاثة نوافذ طرفية منفصلة من **مجلد المشروع الرئيسي**:

```powershell
java -cp httpserver\target\classes WebServer 8080
java -cp httpserver\target\classes WebServer 8081
```

ثم في النافذة الثالثة:

```powershell
java -cp httpclient\target\classes Application
java -cp lab\target\classes JsonLab
java -cp lab\target\classes SchemaMismatchLab
java -cp lab\target\classes JavaSerializationLab
java -cp lab\target\classes JavaExceptionLab
java -cp 'lab\target\classes;lab\target\dependency\*' ProtobufLab
java -cp 'lab\target\classes;lab\target\dependency\*' SchemaRenameLab
java -cp 'lab\target\classes;lab\target\dependency\*' ComparisonLab
```

لتجربة أمر PDF نفسه من PowerShell، استخدم `curl.exe` (وليس اسم الأمر المختصر `curl`):

```powershell
curl.exe -X POST http://localhost:8080/api/analyze -H "Content-Type: application/json" --data-binary '@lab/customer_request.json'
```

## الملفات وربطها بالمهام

| المطلوب | الملف |
|---|---|
| الكود الأصلي مع إكمال Client/Server | `httpclient/src/main/java/*.java` و `httpserver/src/main/java/WebServer.java` |
| 1.1 ملف JSON وتجربة POST | `lab/customer_request.json` و `lab/src/main/java/JsonLab.java` |
| 1.2 مشكلة اختلاف الأنواع | `lab/prices_sent_as_strings.json` و `lab/prices_expected_as_numbers.json` و `SchemaMismatchLab.java` |
| 2.1 صنف Serializable | `lab/src/main/java/CustomerProfile.java` |
| 2.2 التحويل للبايتات والعكس | `lab/src/main/java/JavaSerializationLab.java` |
| 2.3 الحقل غير القابل للتسلسل وفحص البايتات | `Address.java` و `JavaExceptionLab.java` |
| 3.1 المخطط الهيكلي والشيفرة المولّدة | `lab/proto/customer_profile.proto` و `lab/src/main/java/com/distributed/lab/CustomerProfileProto.java` |
| 3.2 إنشاء Protobuf واستعادته | `lab/src/main/java/ProtobufLab.java` |
| تغيير اسم الحقل لدى المستقبل | `lab/proto/customer_profile_receiver_renamed.proto` و`SchemaRenameLab.java` |
| مقارنة الأحجام لنفس الحقول | `lab/same_data_for_comparison.json` و `ComparisonLab.java` |
| التقرير والأسئلة | `report/Network_Communication_Report.docx` و`report/REPORT.md` |

## ملاحظات فنية

- مسار `/api/analyze` الذي أضفناه هو **echo endpoint للتدريب**، وليس خادماً ينتج تحليلاً تجارياً كاملاً أو يفرض JSON Schema.
- قد تختلف أحجام الملفات بين JVM أو إصدارات المكتبات؛ لا تعتمد على أرقام مكتوبة مسبقاً بدل تشغيل `ComparisonLab`.
- `transient` لا يُسلسل، و`static` ينتمي للصف وليس لكل كائن. يجب عدم الوثوق ببيانات Java serialized قادمة من مصدر مجهول.
- **Protobuf لا يوفر التشفير أو توثيق الرسائل**؛ استخدم HTTPS/TLS وحماية الوصول عندما تكون بيانات حساسة.
- ملف `CustomerProfileProto.java` والنسخة ذات الاسم المعدل تم توليدهما من ملفات `.proto` باستخدام `protoc`.

## إعادة توليد ملفات Java من المخططات (اختياري)

من المجلد الرئيسي بعد تثبيت `protoc`:

```powershell
protoc -I=lab/proto --java_out=lab/src/main/java lab/proto/customer_profile.proto
protoc -I=lab/proto --java_out=lab/src/main/java lab/proto/customer_profile_receiver_renamed.proto
```

## بنية المشروع

- `httpserver/`: سيرفر Java الأصلي مع endpoint JSON إضافي.
- `httpclient/`: العميل الأصلي بعد إكمال TODOs، وإرسال المهمات بشكل غير متزامن.
- `lab/`: تجارب PDF الثلاث ومستندات JSON وملفات Protobuf.
- `report/`: تقرير عربي وإجابات أسئلة التقييم.
- `results/`: نتيجة التشغيل التجريبي بعد التنفيذ.
- `run_all.ps1`: سكريبت Windows لتشغيل كل شيء بضغطة واحدة.
