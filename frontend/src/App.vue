<script setup lang="ts">
// App.vue
import {useRoute} from 'vue-router';
import EmptyLayout from "./layouts/EmptyLayout.vue";
import BasicLayout from "./layouts/BasicLayout.vue";
import {computed} from "vue";

const route = useRoute();

const layoutComponent = computed(() => {
  return route.meta.layout === 'empty' ? EmptyLayout : BasicLayout;
});
</script>

<template>
  <component
      :is="layoutComponent"
      :meta="route.meta"
  >
    <router-view v-slot="{ Component }">
      <component :is="Component"/>
    </router-view>
  </component>
</template>