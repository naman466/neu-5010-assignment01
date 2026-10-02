# LLM Evaluation

Produced with an LLM using the prompt in `LLM-Evaluation-prompt.md`, then reviewed and answered by
you. **Both halves are required** — an unread LLM assessment pasted in whole is worth nothing.

---

## Assessment

Paste the LLM's assessment here, in full and unedited. 

I've finished reading the code and tests and checking two edge cases. Here's the review.

All 33 tests pass, based on the JUnit result files in `build/`. A fresh `gradlew check` with Checkstyle is still running in the background, so I haven't confirmed the lint result. I also compiled a small probe to test two edge cases (results in §2).

**Total: 82 / 100**

---

## 1. Immutability and encapsulation — 25/25

- Every field is `private final`: `Animal.java:29-32` and `AgeMonths.java:21`. There are no setters and nothing changes state after construction.
- Every accessor returns either an immutable type (`String`, `Species`, `AgeMonths`, `LocalDate`) or a primitive, so a caller can't change an `Animal` through anything it gets back.
- Nothing is more public than it needs to be. The `AgeMonths` constructor stays private behind `of`.
- `public class Animal` (`Animal.java:27`) is not `final`, so a subclass could override the accessors. That line came with the starter code, and a later lab plans an "Animal family," so I didn't deduct for it.

## 2. Constructor validation — 20/25

| Rule | Present? |
|---|---|
| null name | ✅ `name == null` (line 57) |
| empty / whitespace-only name | ✅ `name.isBlank()` (line 57) |
| null species | ✅ line 61 |
| null age | ✅ line 65 |
| null intake date | ✅ line 69 |
| validate before any assignment | ✅ all checks come before line 73 |
| stored stripped | ⚠️ `this.name = name.trim();` (line 73) |

**−3: the name check and the name cleanup use different rules for whitespace.** `isBlank()` uses Unicode whitespace, but `trim()` removes any character up to U+0020, which includes control characters. My probe confirmed two problems:
- `"\u0000".isBlank()` is `false`, so the name passes the check. Then `trim()` turns it into `""`. `new Animal("\u0000", …)` builds an animal with an empty name, which the Javadoc promises can never happen ("never blank", line 82).
- `"\u2003Luna\u2003".trim()` still has length 6. A Unicode space around the name isn't removed, even though the rule says to strip surrounding whitespace.

**−2: the error messages.**
- `"species cannot be null, was " + species` always ends in `"was null"`. The same goes for age and intake date. The "was" part adds nothing and reads like a template filled in without thought.
- `"name cannot be null or blank, was " + name` gives one message for two different problems. For a blank name the "was" value is invisible whitespace, and for a null name it prints `"was null"`. A reader can't tell which rule failed.

## 3. Correctness — 15/15

`AgeMonths.toString()` (lines 118-148) gives the required output for 0, 1, 11, 12, 23, 24 and 25 months. Singular and plural forms agree, and whole years drop the months part (line 136). `Animal.toString()` produces exactly `Luna (Cat, 1 year, 11 months, intake 2026-09-21)`. The empty-name bug above would print `" (Cat, …)"`, but I already counted that in §2.

## 4. Testing and coverage — 9/15

**Good:**
- `ageJustBelowMaximumHasCorrectBreakdown` (MAX−1 → `"39 years, 11 months"`) is a smart boundary test that the provided suite doesn't have.
- Every `assertThrows` checks for `IntakeException` specifically, not just any `Throwable`.

**Weaknesses:**
- `nullNameMessageMentionsName` adds almost nothing. The provided `theRefusalSaysWhichArgumentWasWrong` already checks that the name message mentions "name", and both cases go through the same `throw` at line 58.
- The `assertTrue(exception.getMessage().contains(...))` calls have no failure message, so a failing test gives only `expected: <true> but was: <false>`.
- The added test classes have no Javadoc saying what gap they fill.

**Three cases nothing tests:**
1. **Plural years with plural months**, for example 26 → `"2 years, 2 months"`. All the combined-format tests (13, 23, 25) have at least one singular part, so a bug that only shows up when both parts are plural would get through.
2. **The maximum's string**: 480 → `"40 years"`. MAX−1 is tested, MAX itself is not.
3. **Names with tabs, newlines or control characters**, such as `"\tLuna\n"` or `"\u0000"`. Testing these would have caught the bug in §2. The introspection names this exact gap ("I would write tests around the whitespace handling").

The error messages for a null age and a null intake date aren't checked at all either.

## 5. Code quality and style — 8/10

- **Delegation: done correctly.** `Animal.toString()` (line 136) relies on `age`'s own `toString()` through string concatenation and never mentions "year" or "month". If the age format changes, `Animal` needs no edit and stays in step automatically.
- **−2: formatter damage to shipped files.** An auto-formatter re-wrapped Javadoc in the middle of sentences:
    - `Animal.java:23-25`: "animals with / identical / fields"
    - `IntakeException.java:8-12`: "a bug / in the / calling code … and / demanding a"

  It also touched `Species.java` and `IntakeException.java`, which the student wasn't meant to edit, so the diff is cluttered with changes that aren't theirs. The added test classes also have no class-level Javadoc.

## 6. Scope discipline and code walk — 5/10

- **Scope: clean.** No inheritance, no collections and no `equals`/`hashCode` were added. The changes to `Species` are only formatting.
- **−5: `introspection.md` never mentions `Species`.** There's no evidence the student understands why it's an `enum` with a `label` field and a `label()` method instead of three constants. Their own code relies on exactly that: `Animal.toString()` prints `Cat` rather than `CAT` because `Species.toString()` returns the label.
- **One factual error in the introspection (not deducted, but worth correcting):** the claim that validating first means "the constructor cannot finalize each field as it goes" is wrong. A `final` field can be assigned anywhere in the constructor. Also, if a constructor throws, the half-built object is never handed to anyone. Validating first matters most when assignments have side effects or `this` leaks out of the constructor, and the student should be able to explain that in the code walk.

---

**Most important thing to do differently next time:** validate and clean up input using the same definition, then test the inputs that sit between the two definitions. This lab is about constructor validation, and the one real bug is a check (`isBlank`) and a cleanup step (`trim`) that disagree. Reading the Javadoc of both methods, plus one test with a tab or control character, would have caught it.

**One thing done genuinely well:** `AgeMonths.toString()` is clear. It handles years and months as separate parts and returns early for the whole-year case. It correctly reuses `years()` and `remainderMonths()` instead of redoing the arithmetic, and the introspection honestly describes simplifying a first version that was harder to follow.

Its scoring categories are the ones in `LLM-Evaluation-prompt.md`, which are the same categories and
the same weights as the rubric in `how-to-submit.md`. If the LLM invents different categories or
weights, say so below rather than silently accepting them.

**Coverage reported:** 100% [line coverage % from `build/reports/jacoco/test/html/index.html`]

---

## Your response

The part that is actually marked. For each point below, a few sentences.

### Where it is right

Which criticisms do you accept? For each, say what you would change and why you agree.

- I accept the criticism from Claude regarding my usage of the trim() method. I should have looked more closely at Java's language and Unicode semantics before using a function in code where validation and input normalization need to be consistent. After further research, I found that trim() removes characters with code points up to U+0020, while isBlank() uses a different definition of whitespace, meaning that the two methods can disagree about what counts as whitespace. In the future, I would use a method whose whitespace semantics match the validation being performed. I did not change the implementation this time because doing so after reading Claude's evaluation would make the evaluation less representative of the code I originally submitted.
- I also agree with the factual error it pointed out in my introspection. A half-initialized object is never handed to anyone and the memory ramifications of the half initialization are miniscule so the focus should have been more on the validation aspect of the design.
### Where it is wrong

Which criticisms do you reject, and on what grounds? LLMs confidently misread code, invent
requirements that are not in the specification, and flag correct code as broken. Disagreeing with a
specific reason is worth more marks here than agreeing with everything.

- I reject Claude's hallucination that I cannot / did not explain the Species class. I understand how that class functions, and it was meant to be part of the code-walk rather than the introspection or the code. 
- I disagree with the understanding that the null test error message is not useful / template. The message specifies what was wrong, and what the user entered. A generic or template message would be "cannot initialize, try again", not the message used in our code.
- I disagree with Claude's characterization of the null-input error messages as unhelpful or merely templated. The message specifies what was wrong and what value was received. A generic or templated message would be something like `"cannot initialize, try again"`, which does not tell the user what caused the problem. Our messages explicitly identify the argument, such as `"species cannot be null, was " + species`. I agree that the wording could be improved, but I do not agree that the messages fail to communicate what went wrong.
- I disagree with the deduction for the lack of Javadoc on my additional test classes. The rubric requires Javadoc purpose statements on public members, while my test classes and test methods are package-private. I therefore don't believe the absence of class-level Javadoc on these tests represents a Checkstyle or rubric violation.
- - I disagree with Claude's characterization that I intentionally modified the provided Species and IntakeException files. Those files were not manually edited by me. The formatting changes were applied automatically by the IDE's Checkstyle/formatting integration using the Checkstyle configuration that had been provided earlier in the course. I recognize that the resulting Git diff still contains changes to those files, but those changes were formatter-generated rather than changes I made to their implementation or behavior.
### What it missed

What do you know is weak in your submission that the assessment did not mention? Volunteering this
costs you nothing and demonstrates you understand your own code.

I think AgeMonths.toString() is still the weakest part of my implementation from a style perspective. I improved it significantly from my first attempt, but there are still several if statements dedicated to singular/plural formatting. I would like to find a way to reduce that branching while keeping the output explicit and readable, rather than assuming that the current implementation is the best possible design.

### What you changed

If you changed anything as a result, say what and why. If you changed nothing, say that and defend
it.

I did not change anything in the code as a result of Claude's assessment. Even though Claude pointed out the issue with my use of trim(), I felt that changing the code after getting the assessment would make the evaluation less representative of what I originally submitted. I also thought about changing the AgeMonths.toString() method to make the conditions cleaner, but I decided not to because the current version works and I am not sure that the alternative is actually easier to understand. I will keep both of these points in mind for future work, especially checking the actual Java documentation before choosing a method.

---

## Declaration

- Which LLM and version you used: Claude Opus 5.5 Medium
- Confirm you understand every line you submitted, regardless of who or what wrote it: Yes
