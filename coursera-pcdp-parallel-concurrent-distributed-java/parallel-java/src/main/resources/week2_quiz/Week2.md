1. Consider the following parallel pseudo-code that use future tasks, as introduced in Lecture 2.1. Select the statement
   below which must be true for this code snippet.

``` java
finish {
   future A = future { S1; return 1;} ;
   future B = future { S2; int a = A.get(); S3; return 3;} ;
   future C = future { int a = A.get(); S4; int b = B.get(); 
                       S5; return 5;} ; 
}
```

<ol type="A">
  <li>S1 can run in parallel with S3</li> - not possible as we get A/S1 before S3
  <li>S2 can run in parallel with S5</li>
  <li>S1, S2, and S4 can all run in parallel with each other</li>
  <li>🟢 S2 can run in parallel with S4</li>
  <li>S4 can run in parallel with S5</li>
</ol>

2. Which of the following dependencies result from the following pseudo-code using futures (as introduced in Lecture
   2.1)?

``` java
finish {
   future A = future { int a = 1; S1; S2; return a;} ;
   future B = future { int b = 2; S3; int a = A.get(); S4; 
                       return a+b;} ;
}
```

<ol type="A">
   <li>S3 depends on S2 having executed</li>
   <li>🟢 S4 depends on S2 having executed</li>
   <li>S2 depends on S4 having executed</li>
   <li>S1 depends on S3 having executed</li>
</ol>

3. You can use futures in the Java Fork-Join framework (as discussed in Lecture 2.2) by implementing which class?

<ol type="A">
   <li>RecursiveAction</li>
   <li>🟢 RecursiveTask</li>
   <li>Future</li>
   <li>FutureTask</li>
</ol>

4. How do you retrieve the value from a future in the Java Fork-Join framework (as discussed in Lecture 2.2) ?

<ol type="A">
   <li>Through the return value provided by explicitly calling a task’s compute() method.</li>
   <li>By the return value of the fork() method of ForkJoinTask.</li>
   <li>🟢 By the return value of the join() method of ForkJoinTask.</li>
   <li>By the return value of the quietlyJoin() method of ForkJoinTask.</li>
</ol>

5. Consider the Pascal’s triangle implementation below, with a triangle containing R rows, with row i containing i
   entries. A triangle with R rows would therefore have $\frac{R(R + 1)}{2}$ total entries. Assuming a memoized
   parallelization strategy like the one below (and discussed in Lecture 2.3), how many futures would be created when
   computing Pascal’s triangle with R rows ?

<ol type="A">
   <li>R</li>
   <li>2 * R</li>
   <li>R(R + 1)</li>
   <li>🟢 $\frac{R(R + 1)}{2}$</li>
</ol>

6. Based on the same Pascal's triangle implementation above, if memoization and futures are used to compute a Pascal’s
   triangle of R rows, how many future get() calls must be made to compute the values for the R rows? Keep in mind the
   special cases of elements on the left and right edges of the triangle. You should ignore the get() calls on the
   second to last line (line 31) in the code example above; only consider the get()s necessary to compute the actual
   contents of the triangle.

<ol type="A">
   <li>🟢 $R^{2} - R$</li>
   <li>🔴 $\frac{R(R + 1)}{2}$</li>
   <li>🔴 R(R+1)</li>
   <li>🔴 2 * R</li>
</ol>

7. Supposed you had a List of Integers in Java: [3, 6, 8, 2, 1, 0]. Which of the stream-based programs below would be
   equivalent to the provided loop-based Java code?
   (Recall that Java streams were introduced in Lecture 2.4.)

> B. 🟢 ```input.stream().filter(v -> v >= 3);```

8. Consider a search algorithm that uses many tasks to examine the search space in parallel. Every task that discovers a
   target value in the search space will set a global boolean flag to true (it is initialized to false). However, no
   task will ever read this global flag during execution, hence there will be no early exit of tasks. The flag will only
   be read after all tasks have terminated.

   Such a program will exhibit which of the following? (Recall that data races, functional determinism, and structural
   determinism were introduced in Lecture 2.5.)

<ol type="A">
   <li>Functional and structural determinism, and no data race</li>
   <li>🟢 Functional and structural determinism, with a data race</li> - data race as the field can be modified by 
the last thread
   <li>Functional but not structural determinism, with a data race</li>
   <li>Structural but not functional determinism, with a data race</li>
</ol>

9. Now consider another search algorithm that uses many tasks to examine the search space in parallel. Each task
   increments a shared integer counter (e.g., count = count +1) when the search is successful.

   Such a program will exhibit which of the following?

<ol type="A">
   <li>Functional and structural determinism, and no data race</li>
   <li>Functional and structural determinism, with a data race</li>
   <li>Functional but not structural determinism, with a data race</li>
   <li>🟢 Structural but not functional determinism, with a data race</li>
</ol>

10. Question 10 Finally, consider a variation of Question 8 in which every task that discovers a target value in the
    search space will set a shared global int variable to the location of the target value that was found
    (the variable is initialized to -1). However, no task will ever read the int variable during execution, hence there
    will be no early exit of tasks. The variable will only be read after all tasks have terminated.  
    Note that, in general, there can be multiple possible locations for the target value, and we assume that any target
    location is acceptable in the final value of the int variable.

<ol type="A">
   <li>Functional and structural determinism, and no data race</li>
   <li>Functional and structural determinism, with a data race</li> 
   <li>🟢 Benign non-determinism, with a data race</li>
</ol>