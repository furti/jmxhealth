var gulp = require('gulp'),
  ts = require('gulp-typescript'),
  concat = require('gulp-concat'),
  tsconfig = require('./tsconfig.json'),
  tsProject = ts.createProject('./tsconfig.json', {
    sortOutput: true
  });

gulp.task('build', function() {
  var tsSources = tsProject.src()
    .pipe(ts(tsProject)).js;

  return tsSources
    .pipe(concat('jmxhealth-tray.js'))
    .pipe(gulp.dest(tsconfig.compilerOptions.outDir));
});

gulp.task('watch', function() {
  gulp.watch(tsconfig.filesGlob, ['build']);
});
