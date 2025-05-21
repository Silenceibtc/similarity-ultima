<script setup lang="ts">
import {ref} from 'vue';
import {useRouter} from "vue-router";

const searchText = ref('');
const activeIds = ref([]);
const activeIndex = ref(0);
const desTagList = ref([]);
const router = useRouter();
let ifSearch = ref(false);
const tagList = [
  {
    text: '性别',
    children: [
      {text: '男', id: '男'},
      {text: '女', id: '女'},
    ],
  },
  {
    text: '年级',
    children: [
      {text: '大一', id: '大一'},
      {text: '大二', id: '大二'},
      {text: '大三', id: '大三'},
      {text: '大四', id: '大四'},
      {text: '研一', id: '研一'},
      {text: '研二', id: '研二'},
      {text: '研三', id: '研三'},
    ],
  },
  {
    text: '语言',
    children: [
      {text: 'java', id: 'java'},
      {text: 'python', id: 'python'},
    ],
  },
];
const onSearch = () => {
  if (!searchText.value) {
    return;
  }
  ifSearch.value = true;
  let children: string[] = [];
  tagList.forEach(tag => {
    const matchedChildren = tag.children.filter(child => child.text.includes(searchText.value));
    if (matchedChildren.length > 0) {
      matchedChildren.forEach(child => children.push(child.text))
    }
  })
  desTagList.value = children;
  if (!desTagList || desTagList.value.length < 1) {
    alert('未找到符合条件的标签')
  }
};
const onCancel = () => {
  desTagList.value = [];
  ifSearch.value = false;
};
const doClose = (tag: string) => {
  activeIds.value = activeIds.value.filter((item) => {
    return tag !== item
  })
}
const clickTag = (tag: string) => {
  if (activeIds.value.includes(tag)) {
    activeIds.value = activeIds.value.filter(item => item != tag)
  } else {
    activeIds.value.push(tag)
  }
}
// 用于判断标签的类型 (空心或实心)
const getTagType = (tag: string) => {
  return !activeIds.value.includes(tag);
};
// 点击搜索跳转搜索结果页并将所选标签传递过去
const doSearchResult = () => {
  router.push({
    path: '/user/list',
    query: {
      tags: activeIds.value,
    }
  })
}
</script>

<template>
  <div>
    <form action="/">
      <van-search
          v-model="searchText"
          show-action
          placeholder="请输入要搜索的标签"
          @search="onSearch"
          @cancel="onCancel"
      />
    </form>
    <van-divider v-if="ifSearch && desTagList.length > 0"
                 :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }"
    >
      可能想搜
    </van-divider>
    <van-space wrap>
      <van-tag v-for="tag in desTagList" :plain="getTagType(tag)" type="primary" @click="clickTag(tag)">
        {{ tag }}
      </van-tag>
    </van-space>
    <van-divider
        :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }"
    >
      已选标签
    </van-divider>
    <van-space wrap>
      <van-tag v-for="tag in activeIds" closeable size="medium" type="primary" @close="doClose(tag)">
        {{ tag }}
      </van-tag>
    </van-space>
    <van-divider
        :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }"
    >
      可选标签
    </van-divider>
    <van-tree-select
        v-model:active-id="activeIds"
        v-model:main-active-index="activeIndex"
        :items="tagList"
        style="height: 250px"
    />
    <div style="padding: 12px;">
      <van-button block type="primary" @click="doSearchResult">搜索</van-button>
    </div>
  </div>
</template>

<style scoped>

</style>