var gulp = require('gulp'),
  ts = require('gulp-typescript'),
  // concat = require('gulp-concat'),
  tsconfig = require('./tsconfig.json'),
  tsProject = ts.createProject('./tsconfig.json', {
    sortOutput: true
  });

gulp.task('html', function() {
  gulp.src('./src/templates/**')
    .pipe(gulp.dest('./target/templates'));
});

gulp.task('ts', function() {
  var tsSources = tsProject.src()
    .pipe(ts(tsProject)).js;

  return tsSources
    // .pipe(concat('jmxhealth-tray.js'))
    .pipe(gulp.dest(tsconfig.compilerOptions.outDir));
});

gulp.task('build', ['ts', 'html']);

gulp.task('watch', ['build'], function() {
  gulp.watch(tsconfig.filesGlob, ['ts']);
});
